package com.antmillion.auth.jwt;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;

  public JwtAuthenticationFilter(JwtProvider jwtProvider) {
    this.jwtProvider = jwtProvider;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {

    // 1) 쿠키에서 Access Token(AT) 읽기 (지금은 쿠키 방식 기준)
    String accessToken = CookieUtil.getCookieValue(request, "AT");

    // 2) 유효하면 SecurityContext에 인증 주입
    if (accessToken != null && jwtProvider.isValid(accessToken)) {
      Claims claims = jwtProvider.parseClaims(accessToken);

      String userId = claims.getSubject();

      var auth = new UsernamePasswordAuthenticationToken(
    		    userId,
    		    null,
    		    java.util.List.of() // 권한 없음
    		    );
      SecurityContextHolder.getContext().setAuthentication(auth);
    }

    filterChain.doFilter(request, response);
  }
}