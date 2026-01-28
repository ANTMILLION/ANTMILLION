package com.antmillion.kis.repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.antmillion.kis.dto.ForeignerOrganization;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class KisFrgnOrgnRedisRepository {
	private static final String KIS_FRGNORGN_NTBY = "kis:frgnorgn-ntby";
    private static final long CACHE_EXPIRE = 600;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void save(String stockCode, ForeignerOrganization data) {
    	String key = KIS_FRGNORGN_NTBY + stockCode;
        redisTemplate.opsForValue().set(key, data, CACHE_EXPIRE, TimeUnit.SECONDS);
    }

    public Optional<ForeignerOrganization> getKisFrgnorgnNtby(String stockCode) {
    	String key = KIS_FRGNORGN_NTBY + stockCode;
        Object data =  redisTemplate.opsForValue().get(key);

        if (data == null) {
            return Optional.empty();
        }

        try {
        	ForeignerOrganization foreignerOrganization = objectMapper.convertValue(
                    data,
                    new  TypeReference<ForeignerOrganization>() {}
            );
            return Optional.of(foreignerOrganization);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
