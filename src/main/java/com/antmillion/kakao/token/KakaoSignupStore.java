package com.antmillion.kakao.token;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class KakaoSignupStore {

    private final StringRedisTemplate redis;

    public KakaoSignupStore(StringRedisTemplate redis) {
        this.redis = redis;
    }

    private String key(String signupKey) {
        return "KAKAO:TMP:" + signupKey;
    }

    public void save(String signupKey, long kakaoId, long ttlSeconds) {
        redis.opsForValue().set(key(signupKey), String.valueOf(kakaoId), Duration.ofSeconds(ttlSeconds));
    }

    public Long get(String signupKey) {
        String v = redis.opsForValue().get(key(signupKey));
        return (v == null || v.isBlank()) ? null : Long.valueOf(v);
    }

    public void delete(String signupKey) {
        redis.delete(key(signupKey));
    }
}
