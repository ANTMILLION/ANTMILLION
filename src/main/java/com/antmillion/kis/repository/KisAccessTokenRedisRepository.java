package com.antmillion.kis.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class KisAccessTokenRedisRepository {

    private static final String KIS_TOKEN_KEY = "kis:access_token";
    
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis에 액세스 토큰 저장
     * @param token 액세스 토큰
     * @param expiresInSeconds 만료 시간(초)
     */
    public void save(String token, long expiresInSeconds) {
        redisTemplate.opsForValue().set(KIS_TOKEN_KEY, token, expiresInSeconds, TimeUnit.SECONDS);
    }

    /**
     * Redis에서 액세스 토큰 조회
     * @return 저장된 토큰 (없거나 만료되면 Empty)
     */
    public Optional<String> findAccessToken() {
        Object token = redisTemplate.opsForValue().get(KIS_TOKEN_KEY);
        if (token instanceof String) {
            return Optional.of((String) token);
        }
        return Optional.empty();
    }

    /**
     * Redis에서 액세스 토큰 삭제
     */
    public void deleteAccessToken() {
        redisTemplate.delete(KIS_TOKEN_KEY);
    }
}
