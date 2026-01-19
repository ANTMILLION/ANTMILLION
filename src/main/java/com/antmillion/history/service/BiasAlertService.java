package com.antmillion.history.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.antmillion.history.dto.BiasAlertDTO;
import com.antmillion.history.mapper.BiasAlertMapper;

/**
 * 매매 편향 경고 서비스
 */
@Service
public class BiasAlertService {
    
    @Autowired
    private BiasAlertMapper biasAlertMapper;
    
    // 편향 경고 기준값
    private static final BigDecimal SAFE_HAVEN_PROFIT_THRESHOLD = new BigDecimal("3.0");  // 3%
    private static final Long SAFE_HAVEN_HOLDING_DAYS = 3L;  // 3일 이내
    
    /**
     * 안전선호 편향 체크
     * @param accountId 계좌 ID
     * @param stockCode 종목 코드
     * @return 편향 경고 정보
     */
    public BiasAlertDTO checkSafeHavenBias(Long accountId, String stockCode) {
        System.out.println("BiasAlertService: 안전선호 체크 시작 - accountId=" + accountId + ", stockCode=" + stockCode);
        
        // 1. 보유 정보 조회
        BiasAlertDTO asset = biasAlertMapper.selectAssetForBiasCheck(accountId, stockCode);
        
        if (asset == null || asset.getQuantity() == null || asset.getQuantity() <= 0) {
            System.out.println("보유하지 않은 종목 또는 수량 0");
            return null;  // 보유하지 않은 종목
        }
        
        System.out.println("보유 정보: " + asset);
        
        // 2. 현재가 조회
        BigDecimal currentPrice = biasAlertMapper.selectCurrentPrice(stockCode);
        System.out.println("현재가: " + currentPrice);
        
        // 3. 수익률 계산
        BigDecimal profitRate = calculateProfitRate(asset.getAvgPrice(), currentPrice);
        System.out.println("수익률: " + profitRate + "%");
        
        // 4. 안전선호 편향 체크
        boolean hasAlert = profitRate.compareTo(SAFE_HAVEN_PROFIT_THRESHOLD) >= 0 
                        && asset.getHoldingDays() != null
                        && asset.getHoldingDays() <= SAFE_HAVEN_HOLDING_DAYS;
        
        System.out.println("경고 여부: " + hasAlert + " (수익률 >= 3% && 보유일수 <= 3일)");
        
        // 5. DTO 빌드 및 반환
        return BiasAlertDTO.builder()
                .stockCode(asset.getStockCode())
                .stockName(asset.getStockName())
                .quantity(asset.getQuantity())
                .avgPrice(asset.getAvgPrice())
                .currentPrice(currentPrice)
                .profitRate(profitRate)
                .purchaseDate(asset.getPurchaseDate())
                .holdingDays(asset.getHoldingDays())
                .biasType(hasAlert ? "SAFE_HAVEN" : null)
                .hasAlert(hasAlert)
                .build();
    }
    
    /**
     * 수익률 계산
     * @param avgPrice 평균 매수가
     * @param currentPrice 현재가
     * @return 수익률 (%)
     */
    private BigDecimal calculateProfitRate(BigDecimal avgPrice, BigDecimal currentPrice) {
        if (avgPrice == null || avgPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal profit = currentPrice.subtract(avgPrice);
        BigDecimal profitRate = profit.divide(avgPrice, 4, RoundingMode.HALF_UP)
                                      .multiply(new BigDecimal("100"));
        
        return profitRate.setScale(2, RoundingMode.HALF_UP);
    }
}