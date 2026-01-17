package com.antmillion.kis.repository;

import com.antmillion.kis.dto.ChartStockPrice;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class KisChartRedisRepository {

    private static final String KIS_PERIOD_CHART_KEY = "stock:chart";
    private static final long CACHE_EXPIRE_HOURS = 24; // 24시간 캐시 유지

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    /***
     * Redis에 차트 데이터 저장
     * @param stockCode 종목코드
     * @param period 기간 : day, week, month, year
     * @param chartData 차트 데이터
     */
    public void save(String stockCode, String period, List<ChartStockPrice> chartData) {
        String key = buildKey(stockCode, period);
        redisTemplate.opsForValue().set(key, chartData, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    public Optional<List<ChartStockPrice>> getChartData(String stockCode, String period) {
        String key = buildKey(stockCode, period);
        Object data = redisTemplate.opsForValue().get(key);

        if (data == null) {
            return Optional.empty();
        }

        try {
            // Redis에서 가져온 데이터를 List<ChartStockPrice>로 변환
            List<ChartStockPrice> chartData = objectMapper.convertValue(
                    data,
                    new TypeReference<List<ChartStockPrice>>() {}
            );
            return Optional.of(chartData);
        } catch (Exception e) {
            return Optional.empty();
        }

    }

    /**
     * Redis에 저장할 키 값 생성
     */
    private String buildKey(String stockCode, String period) {
        return String.format("%s:%s:%s", KIS_PERIOD_CHART_KEY, period, stockCode);
    }



}
