package com.antmillion.auth.jwt;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    public JwtAuthFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

    	// 이미 인증된 요청이면 통과 (AutoRefreshFilter가 인증을 세팅한 경우 포함)
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(req, res);
            return;
        }

        // AT 추출: Authorization 헤더 우선, 없으면 HttpOnly 쿠키(AT) fallback
        String token = TokenResolver.resolveAccessToken(req);

        if (token == null || token.isBlank()) {
            chain.doFilter(req, res);
            return;
        }

        // 토큰이 있지만 유효하지 않으면 인증을 만들지 않고 통과
        if (!jwtProvider.isValid(token)) {
            SecurityContextHolder.clearContext();
            chain.doFilter(req, res);
            return;
        }

        // 유효한 토큰이면: subject(userId)로 Authentication 생성
        Claims claims = jwtProvider.parseClaims(token);
        Long userId = Long.valueOf(claims.getSubject());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(req, res);
    }
}
