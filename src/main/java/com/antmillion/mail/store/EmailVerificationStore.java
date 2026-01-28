package com.antmillion.mail.store;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmailVerificationStore {

    private final StringRedisTemplate redis;

    private final Duration codeTtl;
    private final Duration cooldownTtl;
    private final Duration lockTtl;
    private final Duration ipWindowTtl;
    private final long ipMax;
    private final long maxTries;

    public EmailVerificationStore(
            StringRedisTemplate redis,
            @Value("${mail.verify.code-ttl-seconds:300}") long codeTtlSeconds,
            @Value("${mail.verify.cooldown-seconds:30}") long cooldownSeconds,
            @Value("${mail.verify.lock-ttl-seconds:600}") long lockTtlSeconds,
            @Value("${mail.verify.ip-window-seconds:60}") long ipWindowSeconds,
            @Value("${mail.verify.ip-max:5}") long ipMax,
            @Value("${mail.verify.max-tries:5}") long maxTries
    ) {
        this.redis = redis;
        this.codeTtl = Duration.ofSeconds(codeTtlSeconds);
        this.cooldownTtl = Duration.ofSeconds(cooldownSeconds);
        this.lockTtl = Duration.ofSeconds(lockTtlSeconds);
        this.ipWindowTtl = Duration.ofSeconds(ipWindowSeconds);
        this.ipMax = ipMax;
        this.maxTries = maxTries;
    }

    private String nEmail(String email) {
        return (email == null) ? "" : email.trim().toLowerCase();
    }

    private String codeKey(String email) { return "EMAIL_VERIFY:CODE:" + nEmail(email); }
    private String tryKey(String email) { return "EMAIL_VERIFY:TRY:" + nEmail(email); }
    private String lockKey(String email) { return "EMAIL_VERIFY:LOCK:" + nEmail(email); }
    private String cooldownKey(String email) { return "EMAIL_VERIFY:COOLDOWN:" + nEmail(email); }
    private String ipKey(String ip) { return "EMAIL_VERIFY:IP:" + (ip == null ? "" : ip.trim()); }

    public boolean isLocked(String email) {
        return Boolean.TRUE.equals(redis.hasKey(lockKey(email)));
    }

    public long lockSecondsLeft(String email) {
        Long sec = redis.getExpire(lockKey(email), TimeUnit.SECONDS);
        return (sec == null || sec < 0) ? 0 : sec;
    }

    public boolean acquireCooldown(String email) {
        return Boolean.TRUE.equals(redis.opsForValue().setIfAbsent(cooldownKey(email), "1", cooldownTtl));
    }

    public long cooldownSecondsLeft(String email) {
        Long sec = redis.getExpire(cooldownKey(email), TimeUnit.SECONDS);
        return (sec == null || sec < 0) ? 0 : sec;
    }

    public boolean allowIp(String ip) {
        String key = ipKey(ip);
        Long val = redis.opsForValue().increment(key);
        if (val != null && val == 1L) {
            redis.expire(key, ipWindowTtl);
        }
        return (val != null && val <= ipMax);
    }

    public long ipWindowSecondsLeft(String ip) {
        Long sec = redis.getExpire(ipKey(ip), TimeUnit.SECONDS);
        return (sec == null || sec < 0) ? 0 : sec;
    }

    public void saveCode(String email, String code) {
        // 전송 시: 기존 코드/시도횟수/잠금은 초기화
        redis.opsForValue().set(codeKey(email), code, codeTtl);
        redis.delete(tryKey(email));
        redis.delete(lockKey(email));
    }

    public VerifyResult verifyCode(String email, String code) {
        if (isLocked(email)) {
            return VerifyResult.locked(lockSecondsLeft(email));
        }

        String saved = redis.opsForValue().get(codeKey(email));
        if (saved == null) {
            return VerifyResult.noCode();
        }

        boolean ok = saved.equals(code);
        if (ok) {
            // 성공: 코드/시도횟수 정리
            redis.delete(codeKey(email));
            redis.delete(tryKey(email));
            redis.delete(lockKey(email));
            return VerifyResult.ok();
        }

        // 실패: 시도횟수 증가 + 임계치 초과 시 잠금
        long tries = incTry(email);
        if (tries >= maxTries) {
            redis.delete(codeKey(email));
            redis.delete(tryKey(email));
            redis.opsForValue().set(lockKey(email), "1", lockTtl);
            return VerifyResult.locked(lockTtl.getSeconds());
        }
        return VerifyResult.badCode((maxTries - tries));
    }

    private long incTry(String email) {
        String key = tryKey(email);
        Long val = redis.opsForValue().increment(key);
        if (val != null && val == 1L) {
            redis.expire(key, codeTtl);
        }
        return val == null ? 0 : val;
    }

    public void clearAll(String email) {
        redis.delete(codeKey(email));
        redis.delete(tryKey(email));
        redis.delete(lockKey(email));
        redis.delete(cooldownKey(email));
    }

    public static class VerifyResult {
        private final boolean ok;
        private final String reason;
        private final long secondsLeft;
        private final long remainingTries;

        private VerifyResult(boolean ok, String reason, long secondsLeft, long remainingTries) {
            this.ok = ok;
            this.reason = reason;
            this.secondsLeft = secondsLeft;
            this.remainingTries = remainingTries;
        }

        public static VerifyResult ok() { return new VerifyResult(true, "OK", 0, 0); }
        public static VerifyResult noCode() { return new VerifyResult(false, "NO_CODE", 0, 0); }
        public static VerifyResult badCode(long remainingTries) { return new VerifyResult(false, "BAD_CODE", 0, remainingTries); }
        public static VerifyResult locked(long secondsLeft) { return new VerifyResult(false, "LOCKED", secondsLeft, 0); }

        public boolean isOk() { return ok; }
        public String getReason() { return reason; }
        public long getSecondsLeft() { return secondsLeft; }
        public long getRemainingTries() { return remainingTries; }
    }
}
