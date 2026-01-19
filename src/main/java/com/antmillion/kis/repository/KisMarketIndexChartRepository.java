package com.antmillion.kis.repository;

import com.antmillion.kis.dto.MarketIndexPrice;
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
public class KisMarketIndexChartRepository {

    private static final String KIS_MARKET_INDEX_CHART_KEY = "market:index";
    private static final long CACHE_EXPIRE_HOURS = 24; // 24시간 캐시 유지

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void save(String indexCode, List<MarketIndexPrice> chartData) {
        String key = buildKey(indexCode);
        redisTemplate.opsForValue().set(key, chartData, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    public Optional<List<MarketIndexPrice>> getChartData(String indexCode) {
        String key = buildKey(indexCode);
        Object data = redisTemplate.opsForValue().get(key);

        if (data == null) {
            return Optional.empty();
        }

        try {
            List<MarketIndexPrice> chartData = objectMapper.convertValue(
                    data,
                    new  TypeReference<List<MarketIndexPrice>>() {}
            );
            return Optional.of(chartData);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String buildKey(String indexCode) {
        return String.format("%s:%s", KIS_MARKET_INDEX_CHART_KEY, indexCode);
    }

}
