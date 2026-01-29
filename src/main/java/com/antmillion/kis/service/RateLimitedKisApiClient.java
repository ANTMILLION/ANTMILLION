package com.antmillion.kis.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitedKisApiClient {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Bucket bucket;

    // 20 tokens, 1초마다 refill
    public RateLimitedKisApiClient(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;

        // 1초당 20건 제한 설정
        Bandwidth limit = Bandwidth.simple(20, Duration.ofSeconds(1));
        this.bucket = Bucket.builder()
                .addLimit(limit)
                .build();

        log.info("RateLimitedKisApiClient 초기화: 1초당 20건 제한");
    }
}
