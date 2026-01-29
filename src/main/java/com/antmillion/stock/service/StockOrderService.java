package com.antmillion.stock.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.antmillion.auth.mapper.AccountMapper;
import com.antmillion.stock.dto.StockOrderDTO;
import com.antmillion.stock.dto.StockOrderResponseDTO;
import com.antmillion.stock.mapper.AssetMapper;
import com.antmillion.stock.mapper.StockMapper;
import com.antmillion.stock.mapper.StockOrderMapper;
import com.antmillion.stock.mapper.TradeLogMapper;
import com.antmillion.user.dto.AccountDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockOrderService {

	private final StockOrderMapper stockOrderMapper;
	private final TradeLogMapper tradeLogMapper;
	private final AccountMapper accountMapper;
	private final AssetMapper assetMapper;

	// 주문 생성
    @Transactional
    public boolean createOrder(Long accountId, String stockCode, String transactionType, 
                               String orderType, Integer quantity, Integer orderPrice) {
        
        // 1. 계좌 정보 조회
        AccountDTO account = accountMapper.selectByAccountId(accountId);
        if (account == null) {
            throw new IllegalArgumentException("계좌를 찾을 수 없습니다.");
        }
        
        long totalPrice = (long) orderPrice * quantity;
        
        // 2. 매수/매도 검증(실제 차감 X)
        if ("BUY".equals(transactionType)) {
            // 매수: 잔액 확인
            if (account.getBalance() < totalPrice) {
                throw new IllegalArgumentException("잔액이 부족합니다.");
            }
        } else if ("SELL".equals(transactionType)) {
            // 매도: 보유 수량 확인
            Integer ownedQuantity = assetMapper.getQuantityByAccountAndStock(accountId, stockCode);
            if (ownedQuantity == null || ownedQuantity < quantity) {
                throw new IllegalArgumentException("보유 수량이 부족합니다.");
            }
        }
        
        // 3. 주문 생성
        StockOrderDTO order = new StockOrderDTO();
        order.setAccountId(accountId);
        order.setStockCode(stockCode);
        order.setTransactionType(transactionType);
        order.setOrderType(orderType);
        order.setQuantity(quantity);
        order.setOrderPrice(orderPrice);
        order.setStatus("WAIT");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        return stockOrderMapper.insertOrder(order) > 0;
    }
    
    @Transactional
    public void executeTrade(StockOrderDTO order, int tradeQty, long currentPrice) {
        // 체결된 수량만큼의 총액 계산
        long totalPrice = (long) currentPrice * tradeQty;
        Long accountId = order.getAccountId();
        String stockCode = order.getStockCode();
        Integer orderPrice = (int) currentPrice; // 실제 체결가 적용

        // 체결 로그 기록
        tradeLogMapper.insertTradeLog(order.getOrderId(), currentPrice, tradeQty);
        
        // 총 체결량 확인 및 주문 상태 업데이트
        int totalExecuted = tradeLogMapper.getTotalExecutedQty(order.getOrderId());
        String nextStatus = (totalExecuted >= order.getQuantity()) ? "COMPLETED" : "PARTIAL";
        stockOrderMapper.updateOrderStatus(order.getOrderId(), nextStatus);

        // 잔액 및 자산 업데이트
        if ("BUY".equals(order.getTransactionType())) {
            // [자산 업데이트]
            assetMapper.upsertAssetBuy(accountId, stockCode, tradeQty, orderPrice, totalPrice);
            
            // [잔액 업데이트]
            // account를 새로 조회해서 최신 잔액을 가져와야 함
            AccountDTO account = accountMapper.selectByAccountId(accountId);
            long newBalance = account.getBalance() - totalPrice;
            accountMapper.updateBalance(accountId, newBalance); 
            
            log.info("매수 완료: 잔액 {} → {}", account.getBalance(), newBalance);

        } else if ("SELL".equals(order.getTransactionType())) {
            // [자산 업데이트]
            int rows = assetMapper.updateAssetSell(accountId, stockCode, tradeQty);
            
            if (rows == 0) {
                throw new IllegalArgumentException("보유 수량이 부족하여 매도할 수 없습니다.");
            }
            
            // [잔액 업데이트]
            AccountDTO account = accountMapper.selectByAccountId(accountId);
            long newBalance = account.getBalance() + totalPrice;
            accountMapper.updateBalance(accountId, newBalance);
            
            log.info("매도 완료: 잔액 {} → {}", account.getBalance(), newBalance);
        }
    }

	// 주문 대기 조회
	public List<StockOrderResponseDTO> getWaitOrders(Long accountId) {
		return stockOrderMapper.selectWaitOrders(accountId);
	}
	
	// 주문 취소
	@Transactional
	public boolean cancelOrder(Long orderId, Long accountId) {
	    // 주문 존재 여부 및 현재 상태 확인
	    StockOrderResponseDTO order = stockOrderMapper.getOrderByOrderId(orderId);
	    if (order == null) {
	        throw new IllegalArgumentException("존재하지 않는 주문입니다.");
	    }

	    // 이미 체결 완료되었거나 취소된 주문인지 체크
	    if ("COMPLETED".equals(order.getStatus())) {
	        throw new IllegalStateException("이미 전량 체결된 주문은 취소할 수 없습니다.");
	    }
	    if ("CANCEL".equals(order.getStatus())) {
	        throw new IllegalStateException("이미 취소된 주문입니다.");
	    }

	    // 취소 쿼리 실행
	    int result = stockOrderMapper.cancelOrder(orderId, accountId);
	    
	    if (result > 0) {
	        log.info("주문 취소 완료 - orderId: {}, accountId: {}", orderId, accountId);
	        return true;
	    }
	    return false;
	}
	
	// 주문 정정
	@Transactional
	public boolean modifyOrder(StockOrderDTO orderRequest) {
		StockOrderResponseDTO original = stockOrderMapper.getOrderByOrderId(orderRequest.getOrderId());
	    // 이미 체결된 수량 합산 조회 (TradeLog 이용)
	    int executedQty = tradeLogMapper.getTotalExecutedQty(original.getOrderId());
	    
	    // 이미 체결된 수량보다 적게 정정하는 건 절대 금지
	    if (orderRequest.getQuantity() < executedQty) {
	        throw new IllegalArgumentException("정정 수량은 이미 체결된 수량(" + executedQty + "주)보다 적을 수 없습니다.");
	    }
	    orderRequest.setAccountId(original.getAccountId());
	    
	    return stockOrderMapper.updateOrder(orderRequest) > 0;
	}
}
