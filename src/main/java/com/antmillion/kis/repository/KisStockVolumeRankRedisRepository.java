package com.antmillion.kis.repository;

import com.antmillion.kis.dto.StockVolumeRank;
import com.antmillion.kis.dto.StockVolumeRankRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class KisStockVolumeRankRedisRepository {

    private static final String KIS_VOLUME_RANK = "stock:volume-rank";
    private static final long CACHE_EXPIRE = 60;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void save(List<StockVolumeRank> stockVolumeRanks) {
        redisTemplate.opsForValue().set(KIS_VOLUME_RANK, stockVolumeRanks);
    }

    public Optional<List<StockVolumeRank>> getStockVolumeRanks() {
        Object data =  redisTemplate.opsForValue().get(KIS_VOLUME_RANK);

        if (data == null) {
            return Optional.empty();
        }

        try {
            List<StockVolumeRank> stockVolumeRanks = objectMapper.convertValue(
                    data,
                    new  TypeReference<List<StockVolumeRank>>() {}
            );
            return Optional.of(stockVolumeRanks);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
