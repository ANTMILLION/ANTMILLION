package com.antmillion.auth.jwt;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.antmillion.auth.token.RefreshTokenStore;

import io.jsonwebtoken.Claims;

public class RtCookieAuthFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;
  private final RefreshTokenStore refreshTokenStore;

  public RtCookieAuthFilter(JwtProvider jwtProvider, RefreshTokenStore refreshTokenStore) {
    this.jwtProvider = jwtProvider;
    this.refreshTokenStore = refreshTokenStore;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {

    // 이미 인증되어 있으면 스킵
    Authentication existing = SecurityContextHolder.getContext().getAuthentication();
    if (existing != null && existing.isAuthenticated() && !"anonymousUser".equals(existing.getPrincipal())) {
      chain.doFilter(req, res);
      return;
    }

    String uri = req.getRequestURI();
    String cpath = req.getContextPath();
    String path = (cpath != null && !cpath.isEmpty()) ? uri.substring(cpath.length()) : uri;

    // API는 헤더(AT) 전용
    if (path.startsWith("/api/")) {
      chain.doFilter(req, res);
      return;
    }

    // 정적 리소스는 스킵
    if (path.startsWith("/resources/")) {
      chain.doFilter(req, res);
      return;
    }

    // RT 쿠키로 인증 시도
    String rt = CookieUtil.getCookieValue(req, "RT");
    if (rt == null || rt.isBlank() || !jwtProvider.isValid(rt)) {
      chain.doFilter(req, res);
      return;
    }

    try {
      Claims claims = jwtProvider.parseClaims(rt);
      long userId = Long.parseLong(claims.getSubject());

      // Redis에 저장된 RT와 현재 쿠키 RT가 일치해야만 인정
      String saved = refreshTokenStore.get(userId);
      if (saved == null || !saved.equals(rt)) {
        chain.doFilter(req, res);
        return;
      }

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
      SecurityContextHolder.getContext().setAuthentication(authentication);

    } catch (Exception e) {
      // 실패 시 무시하고 진행
      SecurityContextHolder.clearContext();
    }

    chain.doFilter(req, res);
  }
}
