package com.antmillion.stock.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.antmillion.auth.mapper.AccountMapper;
import com.antmillion.stock.mapper.AssetMapper;
import com.antmillion.user.dto.AccountDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/account")
public class AccountBalanceController {

    private final AccountMapper accountMapper;
    private final AssetMapper assetMapper;

    /**
     * 현재 계좌 잔액 조회 API
     */
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getBalance() {
        Map<String, Object> response = new HashMap<>();
        
        try {
        	Long accountId = currentAccountId();
            
            log.info("잔액 조회 - accountId: {}", accountId);
            
            // 계좌 정보 조회
            AccountDTO account = accountMapper.selectByAccountId(accountId);
            
            if (account != null) {
                response.put("success", true);
                response.put("balance", account.getBalance());
                response.put("accountNumber", account.getAccountNumber());
                log.info("잔액 조회 성공: {} 원", account.getBalance());
            } else {
                response.put("success", false);
                response.put("message", "계좌를 찾을 수 없습니다.");
            }
            
        } catch (Exception e) {
            log.error("잔액 조회 오류", e);
            response.put("success", false);
            response.put("message", "잔액 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 종목의 보유 수량 조회 API
     */
    @GetMapping("/holdings")
    public ResponseEntity<Map<String, Object>> getHoldings(
            @RequestParam String stockCode) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
        	Long accountId = currentAccountId();
            
            log.info("보유 수량 조회 - accountId: {}, stockCode: {}", accountId, stockCode);
            
            // 보유 수량 조회 (SUM으로 합산)
            Integer quantity = assetMapper.getQuantityByAccountAndStock(accountId, stockCode);
            
            response.put("success", true);
            response.put("quantity", quantity != null ? quantity : 0);
            log.info("보유 수량 조회 성공: {} 주", quantity);
            
        } catch (Exception e) {
            log.error("보유 수량 조회 오류", e);
            response.put("success", false);
            response.put("message", "보유 수량 조회 중 오류가 발생했습니다: " + e.getMessage());
            response.put("quantity", 0);
        }
        
        return ResponseEntity.ok(response);
    }
    
    private Long currentAccountId() {
        Long userId = currentUserId();
        if (userId == null) return null;

        AccountDTO account = accountMapper.selectByUserId(userId);
        return (account == null) ? null : account.getAccountId();
    }

    private static Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        try {
            return Long.valueOf(auth.getPrincipal().toString());
        } catch (Exception e) {
            return null;
        }
    }

}