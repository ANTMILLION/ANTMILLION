package com.antmillion.history.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.antmillion.history.dto.BiasAlertDTO;
import com.antmillion.history.dto.BiasType;
import com.antmillion.history.mapper.BiasAlertMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BiasAlertService {
    
    @Autowired
    private BiasAlertMapper biasAlertMapper;
    
    // 안전선호 편향 기준
    private static final BigDecimal SAFE_HAVEN_PROFIT_THRESHOLD = new BigDecimal("3.0");
    private static final int SAFE_HAVEN_HOLDING_DAYS = 3;
    
    /**
     * 안전선호 편향 체크
     */
    public BiasAlertDTO checkSafeHavenBias(Long accountId, String stockCode) {
        log.info("안전선호 체크 시작 - accountId={}, stockCode={}", accountId, stockCode);
        
        // 1. 보유 자산 조회
        BiasAlertDTO asset = biasAlertMapper.selectAssetForBiasCheck(accountId, stockCode);
        
        if (asset == null) {
            log.info("보유하지 않은 종목 - stockCode={}", stockCode);
            return null;
        }
        
        // 2. 현재가 조회
        BigDecimal currentPrice = biasAlertMapper.selectCurrentPrice(stockCode);
        
        // 3. 수익률 계산
        BigDecimal profitRate = calculateProfitRate(asset.getAvgPrice(), currentPrice);
        
        // 4. 편향 조건 체크
        boolean hasAlert = profitRate.compareTo(SAFE_HAVEN_PROFIT_THRESHOLD) >= 0 
                        && asset.getHoldingDays() <= SAFE_HAVEN_HOLDING_DAYS;
        
        log.info("안전선호 체크 결과 - 수익률: {}%, 보유일수: {}일, 경고: {}", 
                 profitRate, asset.getHoldingDays(), hasAlert);
        
        // 5. 결과 반환
        return BiasAlertDTO.builder()
                .stockCode(asset.getStockCode())
                .stockName(asset.getStockName())
                .quantity(asset.getQuantity())
                .avgPrice(asset.getAvgPrice())
                .currentPrice(currentPrice)
                .profitRate(profitRate)
                .purchaseDate(asset.getPurchaseDate())
                .holdingDays(asset.getHoldingDays())
                .biasType(BiasType.SAFE_HAVEN) 
                .hasAlert(hasAlert)
                .build();
    }
    
    /**
     * 손실회피 편향 체크 (향후 구현)
     */
    public BiasAlertDTO checkLossAversionBias(Long accountId, String stockCode) {
        // TODO: 구현 예정
        // 조건: 수익률 < -10% AND 보유 기간 > 30일
        return null;
    }
    
    /**
     * 확증 편향 체크 (향후 구현)
     */
    public BiasAlertDTO checkConfirmationBias(Long accountId, String stockCode) {
        // TODO: 구현 예정
        // 조건: 같은 종목 반복 매수
        return null;
    }
    
    /**
     * 수익률 계산
     */
    private BigDecimal calculateProfitRate(BigDecimal avgPrice, BigDecimal currentPrice) {
        if (avgPrice == null || avgPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal profit = currentPrice.subtract(avgPrice);
        BigDecimal rate = profit.divide(avgPrice, 4, RoundingMode.HALF_UP)
                                .multiply(new BigDecimal("100"))
                                .setScale(2, RoundingMode.HALF_UP);
        return rate;
    }
}