package com.antmillion.mail.store;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmailVerificationStore {

    private final StringRedisTemplate redis;
    private final Duration codeTtl;
    private final Duration verifiedTtl;

    public EmailVerificationStore(
            StringRedisTemplate redis,
            @Value("${mail.verify.code-ttl-seconds:300}") long codeTtlSeconds,
            @Value("${mail.verify.verified-ttl-seconds:1800}") long verifiedTtlSeconds
    ) {
        this.redis = redis;
        this.codeTtl = Duration.ofSeconds(codeTtlSeconds);
        this.verifiedTtl = Duration.ofSeconds(verifiedTtlSeconds);
    }

    private String codeKey(String email) { return "EMAIL_VERIFY:CODE:" + email; }
    private String okKey(String email) { return "EMAIL_VERIFY:OK:" + email; }

    public void saveCode(String email, String code) {
        redis.opsForValue().set(codeKey(email), code, codeTtl);
        // 재발송 시 이전 verified 흔적은 지워주는 편이 안전
        redis.delete(okKey(email));
    }

    public boolean verifyCode(String email, String code) {
        String saved = redis.opsForValue().get(codeKey(email));
        if (saved == null) return false;

        boolean ok = saved.equals(code);
        if (ok) {
            redis.delete(codeKey(email));
            redis.opsForValue().set(okKey(email), "1", verifiedTtl);
        }
        return ok;
    }

    public boolean isVerified(String email) {
        return redis.hasKey(okKey(email));
    }

    public void clear(String email) {
        redis.delete(codeKey(email));
        redis.delete(okKey(email));
    }
}
