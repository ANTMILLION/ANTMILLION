package com.antmillion.auth.token;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenStore {

  private final StringRedisTemplate redis;

  public RefreshTokenStore(StringRedisTemplate redis) {
    this.redis = redis;
  }

  private String key(long userId) {
    return "RT:" + userId;
  }

  public void save(long userId, String refreshToken, long ttlSeconds) {
    redis.opsForValue().set(key(userId), refreshToken, Duration.ofSeconds(ttlSeconds));
  }

  public String get(long userId) {
    return redis.opsForValue().get(key(userId));
  }

  public void delete(long userId) {
    redis.delete(key(userId));
  }
}