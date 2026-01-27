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

        String token = resolveToken(req);

        if (token == null || token.isBlank()) {
            chain.doFilter(req, res);
            return;
        }

        if (!jwtProvider.isValid(token)) {
            SecurityContextHolder.clearContext();
            chain.doFilter(req, res);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            Claims claims = jwtProvider.parseClaims(token);
            Long userId = Long.valueOf(claims.getSubject());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(req, res);
    }

    private String resolveToken(HttpServletRequest req) {
        // 1) Authorization: Bearer <AT>
        String bearer = req.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        // 2) HttpOnly 쿠키 "AT" fallback (JSP 화면 요청용)
        String atCookie = CookieUtil.getCookieValue(req, "AT");
        if (atCookie != null && !atCookie.isBlank()) {
            return atCookie;
        }
        return null;
    }
}
