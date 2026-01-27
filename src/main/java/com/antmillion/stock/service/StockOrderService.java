package com.antmillion.stock.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.antmillion.auth.mapper.AccountMapper;
import com.antmillion.stock.dto.StockOrderDTO;
import com.antmillion.stock.mapper.AssetMapper;
import com.antmillion.stock.mapper.StockOrderMapper;
import com.antmillion.user.dto.AccountDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockOrderService {

    private final StockOrderMapper stockOrderMapper;
    private final AccountMapper accountMapper;
    private final AssetMapper assetMapper;

    @Transactional
    public boolean createOrder(Long accountId, String stockCode, String transactionType, 
                               String orderType, Integer quantity, Integer orderPrice) {
        
        // 1. 계좌 정보 조회
        AccountDTO account = accountMapper.selectByAccountId(accountId);
        if (account == null) {
            throw new IllegalArgumentException("계좌를 찾을 수 없습니다.");
        }
        
        long totalPrice = (long) orderPrice * quantity;
        
        // 2. 매수/매도 검증
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
        order.setStatus("COMPLETED");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        int result = stockOrderMapper.insertOrder(order);
        log.info("주문 저장 완료 - orderId: {}", order.getOrderId());
        
        // 4. 잔액 및 자산 업데이트
        if ("BUY".equals(transactionType)) {
            // 매수: 잔액 차감
            long newBalance = account.getBalance() - totalPrice;
            accountMapper.updateBalance(accountId, newBalance);
            log.info("잔액 차감: {} → {}", account.getBalance(), newBalance);
            
            // 자산 추가
            assetMapper.insertAsset(accountId, stockCode, quantity, orderPrice, totalPrice);
            log.info("자산 추가: {} 종목 {} 주 매수", stockCode, quantity);
            
        } else if ("SELL".equals(transactionType)) {
            // 매도: 잔액 증가
            long newBalance = account.getBalance() + totalPrice;
            accountMapper.updateBalance(accountId, newBalance);
            log.info("잔액 증가: {} → {}", account.getBalance(), newBalance);
            
            // 자산 차감
            int remaining = quantity;
            while (remaining > 0) {
                int decreased = assetMapper.decreaseAssetQuantity(accountId, stockCode, remaining);
                if (decreased == 0) {
                    break; // 더 이상 차감할 자산 없음
                }
                remaining -= decreased;
            }
            log.info("자산 차감: {} 종목 {} 주 매도", stockCode, quantity);
        }
        
        return result > 0;
    }
}