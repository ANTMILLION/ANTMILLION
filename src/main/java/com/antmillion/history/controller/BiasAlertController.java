package com.antmillion.history.controller;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.antmillion.auth.mapper.AccountMapper;
import com.antmillion.history.dto.BiasAlertDTO;
import com.antmillion.history.service.BiasAlertService;
import com.antmillion.user.dto.AccountDTO;

import lombok.RequiredArgsConstructor;

/**
 * 매매 편향 경고 API 컨트롤러
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bias-alert")
public class BiasAlertController {

    private final BiasAlertService biasAlertService;
    private final AccountMapper accountMapper;

    @GetMapping("/check")
    public ResponseEntity<BiasAlertDTO> checkBias(@RequestParam String stockCode) {
        Long userId = currentUserId();
        Long accountId = currentAccountId(userId);
        if (userId == null || accountId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        BiasAlertDTO result = biasAlertService.checkRiskAversionBias(accountId, stockCode, userId);
        return (result == null) ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    @GetMapping("/check-loss-aversion")
    public ResponseEntity<BiasAlertDTO> checkLossAversion(@RequestParam String stockCode) {
        Long userId = currentUserId();
        Long accountId = currentAccountId(userId);
        if (userId == null || accountId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        BiasAlertDTO result = biasAlertService.checkLossAversionBias(accountId, stockCode, userId);
        return (result == null) ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    @GetMapping("/check-sunk-cost")
    public ResponseEntity<BiasAlertDTO> checkSunkCost(@RequestParam String stockCode) {
        Long userId = currentUserId();
        Long accountId = currentAccountId(userId);
        if (userId == null || accountId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        BiasAlertDTO result = biasAlertService.checkSunkCostBias(accountId, stockCode, userId);
        return (result == null) ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    @GetMapping("/check-fomo")
    public ResponseEntity<BiasAlertDTO> checkFomo(
            @RequestParam String stockCode,
            @RequestParam(required = false) BigDecimal changeRate) {

        Long userId = currentUserId();
        Long accountId = currentAccountId(userId);
        if (userId == null || accountId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        BiasAlertDTO result = biasAlertService.checkFomoBias(accountId, stockCode, userId, changeRate);
        return (result == null) ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    private Long currentAccountId(Long userId) {
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
