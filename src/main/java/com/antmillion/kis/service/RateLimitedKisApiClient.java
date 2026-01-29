package com.antmillion.kis.service;

import com.antmillion.kis.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
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


    // Rate Limit이 적용된 API 호출 (Redis 캐싱 O)
    public <T> T callWithCache(String cacheKey, Supplier<T> apiCall, long ttl) {
        // 토큰 소비 시도
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // 토큰 있음 → API 호출
            log.debug("Rate limit OK. Remaining tokens: {}", probe.getRemainingTokens());
            T result = apiCall.get();

            // Redis 캐싱 (cacheKey가 null이 아닌 경우만)
            if (cacheKey != null && result != null) {
                redisTemplate.opsForValue().set(cacheKey, result, ttl, TimeUnit.SECONDS);
                log.debug("Redis에 캐싱: key={}, ttl={}초", cacheKey, ttl);
            }

            return result;
        } else {
            // 토큰 없음 → Redis 캐시 조회
            log.warn("Rate limit exceeded. Trying Redis cache: key={}", cacheKey);

            if (cacheKey != null) {
                @SuppressWarnings("unchecked")
                T cached = (T) redisTemplate.opsForValue().get(cacheKey);

                if (cached != null) {
                    log.info("Redis 캐시 반환: key={}", cacheKey);
                    return cached;
                }
            }

            // 캐시도 없음 → 429 예외 발생
            long nanosToWait = probe.getNanosToWaitForRefill();
            log.error("Rate limit exceeded and no cache available. Wait: {}ms", nanosToWait / 1_000_000);
            throw new RateLimitExceededException(nanosToWait);
        }
    }

    // Rate Limit이 적용된 API 호출 (Redis 캐싱 X)
    public <T> T callWithoutCache(Supplier<T> apiCall) {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            log.debug("Rate limit OK (no cache). Remaining tokens: {}", probe.getRemainingTokens());
            return apiCall.get();
        } else {
            // 캐시 없이 바로 429 예외
            long nanosToWait = probe.getNanosToWaitForRefill();
            log.error("Rate limit exceeded (no cache). Wait: {}ms", nanosToWait / 1_000_000);
            throw new RateLimitExceededException(nanosToWait);
        }
    }

    // 남은 토큰 수 조회
    public long getAvailableTokens() {
        return bucket.getAvailableTokens();
    }

}
