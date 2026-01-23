package com.antmillion.auth.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.antmillion.auth.jwt.CookieUtil;
import com.antmillion.auth.jwt.JwtProvider;
import com.antmillion.auth.service.SignService;
import com.antmillion.auth.service.SignService.TokenPair;
import com.antmillion.auth.token.RefreshTokenStore;

import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/auth")
public class AuthTokenController {

  private final JwtProvider jwtProvider;
  private final RefreshTokenStore refreshTokenStore;
  private final SignService signService;

  public AuthTokenController(JwtProvider jwtProvider, RefreshTokenStore refreshTokenStore, SignService signService) {
    this.jwtProvider = jwtProvider;
    this.refreshTokenStore = refreshTokenStore;
    this.signService = signService;
  }

  @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, Object> refresh(HttpServletRequest req, HttpServletResponse res) {
    String rt = CookieUtil.getCookieValue(req, "RT");
    if (rt == null || rt.isBlank() || !jwtProvider.isValid(rt)) {
      res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return Map.of("ok", false, "message", "NO_REFRESH");
    }

    try {
      Claims claims = jwtProvider.parseClaims(rt);
      long userId = Long.parseLong(claims.getSubject());

      String saved = refreshTokenStore.get(userId);
      if (saved == null || !saved.equals(rt)) {
        CookieUtil.deleteCookie(res, "RT");
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return Map.of("ok", false, "message", "REFRESH_MISMATCH");
      }

      // rotate: 새 AT/RT 발급 + Redis 갱신
      TokenPair tokens = signService.issueTokensByUserId(userId);

      // RT는 쿠키로만 저장
      CookieUtil.addHttpOnlyCookie(res, "RT", tokens.getRefreshToken(), tokens.getRefreshTtlSeconds());

      // AT는 헤더 + JSON으로만 전달 (쿠키 X)
      res.setHeader("Authorization", "Bearer " + tokens.getAccessToken());

      return Map.of(
          "ok", true,
          "accessToken", tokens.getAccessToken(),
          "accessTtlSeconds", tokens.getAccessTtlSeconds()
      );

    } catch (Exception e) {
      res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return Map.of("ok", false, "message", "REFRESH_FAIL");
    }
  }
}
