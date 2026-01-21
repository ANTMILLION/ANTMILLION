package com.antmillion.history.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.antmillion.history.dto.BiasAlertDTO;
import com.antmillion.history.service.BiasAlertService;

import lombok.RequiredArgsConstructor;

/**
 * 매매 편향 경고 API 컨트롤러
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bias-alert")
public class BiasAlertController {


	private final BiasAlertService biasAlertService;

    /**
     * 안전선호 편향 체크
     * 경고가 감지되는 순간 자동 저장되게 변경
     *
     * 임시로 userId 기본값 1 처리 (나중에 로그인 붙이면 세션/토큰에서 가져오면 됨)
     */
    @GetMapping("/check")
    public ResponseEntity<BiasAlertDTO> checkBias(
            @RequestParam(required = false, defaultValue = "1") Long accountId,
            @RequestParam(required = false, defaultValue = "1") Long userId,
            @RequestParam String stockCode) {

        System.out.println("BiasAlertController: API 호출 - accountId=" + accountId + ", userId=" + userId + ", stockCode=" + stockCode);

        BiasAlertDTO result = biasAlertService.checkRiskAversionBias(accountId, stockCode, userId);

        if (result == null) {
            System.out.println("결과: 보유하지 않은 종목 (204 No Content)");
            return ResponseEntity.noContent().build();
        }

        System.out.println("결과: " + result);
        return ResponseEntity.ok(result);
    }

}
