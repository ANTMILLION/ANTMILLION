package com.antmillion.history.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.antmillion.history.dto.BiasAlertDTO;
import com.antmillion.history.service.BiasAlertService;

/**
 * 매매 편향 경고 API 컨트롤러
 */
@RestController
@RequestMapping("/api/bias-alert")
public class BiasAlertController {
    
    @Autowired
    private BiasAlertService biasAlertService;
    
    /**
     * 안전선호 편향 체크
     * @param accountId 계좌 ID (기본값: 1)
     * @param stockCode 종목 코드
     * @return 편향 경고 정보
     */
    @GetMapping("/check")
    public ResponseEntity<BiasAlertDTO> checkBias(
            @RequestParam(required = false, defaultValue = "1") Long accountId,
            @RequestParam String stockCode) {
        
        System.out.println("BiasAlertController: API 호출 - accountId=" + accountId + ", stockCode=" + stockCode);
        
        BiasAlertDTO result = biasAlertService.checkSafeHavenBias(accountId, stockCode);
        
        if (result == null) {
            System.out.println("결과: 보유하지 않은 종목 (204 No Content)");
            return ResponseEntity.noContent().build();
        }
        
        System.out.println("결과: " + result);
        return ResponseEntity.ok(result);
    }
}