package com.antmillion.history.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.antmillion.history.dto.BiasAlertDTO;
import com.antmillion.history.dto.BiasType;
import com.antmillion.history.dto.HistoryDTO;
import com.antmillion.history.mapper.BiasAlertMapper;
import com.antmillion.history.mapper.HistoryMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BiasAlertService {

	    private final BiasAlertMapper biasAlertMapper;
	    private final HistoryMapper historyMapper;

    // 위험회피 편향 기준
    private static final BigDecimal RISK_AVERSION_PROFIT_THRESHOLD = new BigDecimal("3.0");
    private static final int RISK_AVERSION_HOLDING_DAYS = 3;
    
    // 손실회피 편향 기준
    private static final BigDecimal LOSS_AVERSION_LOSS_THRESHOLD = new BigDecimal("-7.0");
    
    // 매몰비용오류 편향 기준
    private static final BigDecimal SUNK_COST_LOSS_THRESHOLD = new BigDecimal("-10.0");
    private static final int SUNK_COST_HOLDING_DAYS = 21;

    /**
     * 위험회피 편향 체크
     * 경고가 감지되는 순간 자동으로 history에 저장되게 변경
     */
    public BiasAlertDTO checkRiskAversionBias(Long accountId, String stockCode, Long userId) {
        log.info("위험회피 체크 시작 - accountId={}, stockCode={}, userId={}", accountId, stockCode, userId);

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
        boolean hasAlert = profitRate.compareTo(RISK_AVERSION_PROFIT_THRESHOLD) >= 0
                && asset.getHoldingDays() <= RISK_AVERSION_HOLDING_DAYS;

        log.info("위험회피 체크 결과 - 수익률: {}%, 보유일수: {}일, 경고: {}",
                profitRate, asset.getHoldingDays(), hasAlert);

        // 5. 결과 DTO 만들기 (★ userId 반드시 세팅)
        BiasAlertDTO result = BiasAlertDTO.builder()
                .userId(userId)
                .stockCode(asset.getStockCode())
                .stockName(asset.getStockName())
                .quantity(asset.getQuantity())
                .avgPrice(asset.getAvgPrice())
                .currentPrice(currentPrice)
                .profitRate(profitRate)
                .purchaseDate(asset.getPurchaseDate())
                .holdingDays(asset.getHoldingDays())
                .biasType(BiasType.RISK_AVERSION)
                .hasAlert(hasAlert)
                .build();

        //핵심: 경고가 뜨는 순간 자동 저장
        if (hasAlert) {
            saveBiasAlert(result);
        }

        return result;
    }

    /**
     * 손실회피 편향 체크
     * 조건: 수익률 -7% 이하 && 최근 5거래일간 매도 이력 없음
     */
    public BiasAlertDTO checkLossAversionBias(Long accountId, String stockCode, Long userId) {
        log.info("손실회피 체크 시작 - accountId={}, stockCode={}, userId={}", accountId, stockCode, userId);

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

        // 4. 최근 5거래일 매도 이력 체크
        boolean hasRecentSell = biasAlertMapper.checkRecentSellHistory(userId, stockCode);

        // 5. 편향 조건 체크: 수익률 -7% 이하 && 최근 5거래일 매도 이력 없음
        boolean hasAlert = profitRate.compareTo(LOSS_AVERSION_LOSS_THRESHOLD) <= 0
                && !hasRecentSell;

        log.info("손실회피 체크 결과 - 수익률: {}%, 최근매도이력: {}, 경고: {}",
                profitRate, hasRecentSell, hasAlert);

        // 6. 결과 DTO 만들기
        BiasAlertDTO result = BiasAlertDTO.builder()
                .userId(userId)
                .stockCode(asset.getStockCode())
                .stockName(asset.getStockName())
                .quantity(asset.getQuantity())
                .avgPrice(asset.getAvgPrice())
                .currentPrice(currentPrice)
                .profitRate(profitRate)
                .purchaseDate(asset.getPurchaseDate())
                .holdingDays(asset.getHoldingDays())
                .biasType(BiasType.LOSS_AVERSION)
                .hasAlert(hasAlert)
                .build();

        // 7. 경고가 뜨는 순간 자동 저장
        if (hasAlert) {
            saveBiasAlert(result);
        }

        return result;
    }

    /**
     * 매몰비용오류 편향 체크
     * 조건: 수익률 -10% 이하 && 보유기간 21일 이상
     * 타이밍: 페이지 로드(상시) + 매수 탭 전환 시
     */
    public BiasAlertDTO checkSunkCostBias(Long accountId, String stockCode, Long userId) {
        log.info("매몰비용오류 체크 시작 - accountId={}, stockCode={}, userId={}", accountId, stockCode, userId);

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

        // 4. 편향 조건 체크: 수익률 -10% 이하 && 보유기간 21일 이상
        boolean hasAlert = profitRate.compareTo(SUNK_COST_LOSS_THRESHOLD) <= 0
                && asset.getHoldingDays() >= SUNK_COST_HOLDING_DAYS;

        log.info("매몰비용오류 체크 결과 - 수익률: {}%, 보유일수: {}일, 경고: {}",
                profitRate, asset.getHoldingDays(), hasAlert);

        // 5. 결과 DTO 만들기
        BiasAlertDTO result = BiasAlertDTO.builder()
                .userId(userId)
                .stockCode(asset.getStockCode())
                .stockName(asset.getStockName())
                .quantity(asset.getQuantity())
                .avgPrice(asset.getAvgPrice())
                .currentPrice(currentPrice)
                .profitRate(profitRate)
                .purchaseDate(asset.getPurchaseDate())
                .holdingDays(asset.getHoldingDays())
                .biasType(BiasType.SUNK_COST)
                .hasAlert(hasAlert)
                .build();

        // 6. 경고가 뜨는 순간 자동 저장
        if (hasAlert) {
            saveBiasAlert(result);
        }

        return result;
    }

    /**
     * 심리 경고 저장
     * 5분 중복 방지 로직 추가
     */
    public void saveBiasAlert(BiasAlertDTO biasAlert) {
        log.info("심리 경고 저장 시작 - {}", biasAlert);

        // 안전장치: userId 없으면 저장 못함
        if (biasAlert.getUserId() == null) {
            throw new IllegalArgumentException("userId가 null입니다. checkRiskAversionBias에서 userId를 전달/세팅해야 합니다.");
        }

        // 마지막 경고 조회
        HistoryDTO lastAlert = historyMapper.getLastAlert(
            biasAlert.getUserId(),
            biasAlert.getStockCode(),
            biasAlert.getBiasType().getCode()
        );

        // 5분 체크
        if (lastAlert != null && lastAlert.getTime() != null) {
            Date lastTimeDate = lastAlert.getTime();
            LocalDateTime lastTime = new java.sql.Timestamp(lastTimeDate.getTime()).toLocalDateTime();
            LocalDateTime now = LocalDateTime.now();
            long minutesDiff = ChronoUnit.MINUTES.between(lastTime, now);
            
            if (minutesDiff < 5) {
                log.info("5분 이내 중복 경고 - 저장 생략 (마지막 경고: {}분 전)", minutesDiff);
                return;
            }
        }

        // 5분 지났으면 저장
        HistoryDTO history = HistoryDTO.builder()
                .userId(biasAlert.getUserId())
                .orderId(null)
                .stockCode(biasAlert.getStockCode())
                .biasType(getBiasTypeString(biasAlert.getBiasType()))
                .messageDetail(generateAlertMessage(biasAlert))
                .isRead(false)
                .build();

        historyMapper.insertHistory(history);
        log.info("심리 경고 저장 완료");
    }

    /**
     * BiasType enum을 String으로 변환
     */
    private String getBiasTypeString(BiasType biasType) {
        if (biasType == null) return "UNKNOWN";
        return biasType.getCode(); 
    }

    /**
     * 경고 메시지 생성
     */
    private String generateAlertMessage(BiasAlertDTO biasAlert) {
        if (biasAlert.getBiasType() != null) {
            return biasAlert.getBiasType().getMessage();
        }
        return "심리적 편향이 감지되었습니다. 투자 결정을 재검토해보세요.";
    }

    /**
     * 수익률 계산
     */
    private BigDecimal calculateProfitRate(BigDecimal avgPrice, BigDecimal currentPrice) {
        if (avgPrice == null || avgPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal profit = currentPrice.subtract(avgPrice);
        return profit.divide(avgPrice, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }
}