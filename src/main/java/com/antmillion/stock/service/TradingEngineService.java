package com.antmillion.stock.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.antmillion.stock.dto.AssetDTO;
import com.antmillion.stock.mapper.RealizedPnlMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.antmillion.stock.dto.StockOrderResponseDTO;
import com.antmillion.stock.mapper.AssetMapper;
import com.antmillion.stock.mapper.StockOrderMapper;
import com.antmillion.stock.mapper.TradeLogMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradingEngineService {
    private final StockOrderMapper stockOrderMapper;
    private final TradeLogMapper tradeLogMapper;
    private final AssetMapper assetMapper;
    private final RealizedPnlMapper realizedPnlMapper;

    @Transactional
    public void processExecution(String stockCode, int currentPrice, int availableQty) {
        // 체결 가능한 주문 리스트 
        List<StockOrderResponseDTO> orders = stockOrderMapper.findExecutableOrders(stockCode, currentPrice);
        
        int remainingMarketQty = availableQty; // 시장 호가 잔량

        for (StockOrderResponseDTO order : orders) {
            if (remainingMarketQty <= 0) break;

            int unexecutedQty = order.getUnexecutedQuantity();
            // 이번에 체결할 수량 (시장 물량 vs 주문 미체결량 중 최소값)
            int actualExecQty = Math.min(unexecutedQty, remainingMarketQty);

            // 1. 체결 로그(trade_log) 기록 (trade_id 반환 필요)
            Long tradeId = tradeLogMapper.insertTradeLog(order.getOrderId(), currentPrice, actualExecQty);

            // 3. 주문 상태 업데이트 (누적 체결량 다시 확인하여 상태 변경)
            int totalExecutedSoFar = order.getExecutedQuantity() + actualExecQty;
            String status = (totalExecutedSoFar >= order.getQuantity()) ? "COMPLETED" : "PARTIAL";
            stockOrderMapper.updateOrderStatus(order.getOrderId(), status);

            // 4. 자산 및 잔액 반영
            long tradeAmount = (long)currentPrice * actualExecQty;
            if ("BUY".equals(order.getTransactionType())) {
            	// minusBalance의 리턴값(영향받은 행 수)을 확인해서 잔액 부족 체크 필수
            	int rows = assetMapper.minusBalance(order.getAccountId(), tradeAmount);
                if (rows == 0) {
                    // 잔액이 부족하면 런타임 예외를 던져서 이 주문 전체를 롤백(Rollback)시킴
                    throw new RuntimeException("잔액 부족으로 체결 불가");
                }
                assetMapper.upsertAssetBuy(order.getAccountId(), stockCode, actualExecQty, currentPrice, tradeAmount);
            } else {
                // ===== 매도: 실현손익 계산 및 저장 (보유주식 차감 전) =====
                long costBasis = 0;
                AssetDTO asset = assetMapper.getAssetForSell(order.getAccountId(), stockCode);
                if (asset != null) {
                    long totalBuyAmount = asset.getPurchaseAmount();
                    int holdingQty = asset.getQuantity();
                    double avgPrice = asset.getAvgPrice();

                    // 차감원가 계산
                    if (actualExecQty >= holdingQty) {
                        // 전량매도 : 차감원가 = 남은 총 매수금액 전부
                        costBasis = totalBuyAmount;
                    } else {
                        // 부분매도 : 차감원가 = floor(매입금액 * 매도수량) / 보유수량
                        costBasis = (long) Math.floor((double) totalBuyAmount * actualExecQty / holdingQty);
                    }

                    // 실현손익 = 매도금액 - 차감원가
                    long realizedProfit = tradeAmount - costBasis;

                    // 실현손익 테이블에 저장
                    String sellDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    realizedPnlMapper.insertRealizedPnl(
                        stockCode,
                        tradeId,
                            order.getAccountId(),
                            sellDate,
                            actualExecQty,
                            tradeAmount,
                            costBasis,
                            realizedProfit,
                            avgPrice
                    );
                }

                // 1. 매도: 주식 출고 및 잔액 입금
                assetMapper.updateAssetSellWithCostBasis(order.getAccountId(), stockCode, actualExecQty, costBasis);
                assetMapper.addBalance(order.getAccountId(), tradeAmount);
                
                // 2. DB에 반영된 최신 수량을 다시 확인
                Integer currentQty = assetMapper.getQuantityByAccountAndStock(order.getAccountId(), stockCode);
                
                // 3. 수량이 0이거나 null이면 확실히 삭제 처리
                if (currentQty == null || currentQty <= 0) {
                    assetMapper.deleteZeroQuantityAsset(order.getAccountId(), stockCode);
                }
            }

            remainingMarketQty -= actualExecQty; // 시장 물량 소진
            log.info("주문ID {}: {}주 체결 (남은 시장물량: {})", order.getOrderId(), actualExecQty, remainingMarketQty);
        }
    }
}
