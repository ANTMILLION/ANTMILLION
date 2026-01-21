package com.antmillion.auth.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

  private final SecretKey key;
  private final long accessTtlSeconds;
  private final long refreshTtlSeconds;

  public JwtProvider(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.access-ttl-seconds}") long accessTtlSeconds,
      @Value("${jwt.refresh-ttl-seconds}") long refreshTtlSeconds
  ) {
	this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    this.accessTtlSeconds = accessTtlSeconds;
    this.refreshTtlSeconds = refreshTtlSeconds;
  }

  public String createAccessToken(Long userId, Integer rankId, String provider, String jti) {
	  long now = System.currentTimeMillis();
	  return Jwts.builder()
	      .setSubject(String.valueOf(userId))
	      .setId(jti)
	      .claim("rankId", rankId)      // 선택
	      .claim("provider", provider)  // 선택
	      .setIssuedAt(new Date(now))
	      .setExpiration(new Date(now + accessTtlSeconds * 1000))
	      .signWith(key)
	      .compact();
	}

  public String createRefreshToken(Long userId, String jti) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .setSubject(String.valueOf(userId))
        .setId(jti)
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + refreshTtlSeconds * 1000))
        .signWith(key)
        .compact();
  }

  public Claims parseClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public boolean isValid(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public long getAccessTtlSeconds() {
    return accessTtlSeconds;
  }

  public long getRefreshTtlSeconds() {
    return refreshTtlSeconds;
  }
}