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

import com.antmillion.auth.service.SignService;
import com.antmillion.auth.service.SignService.TokenPair;
import com.antmillion.auth.token.RefreshTokenStore;

import io.jsonwebtoken.Claims;

public class AutoRefreshFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final RefreshTokenStore refreshTokenStore;
    private final SignService signService;

    public AutoRefreshFilter(JwtProvider jwtProvider, RefreshTokenStore refreshTokenStore, SignService signService) {
        this.jwtProvider = jwtProvider;
        this.refreshTokenStore = refreshTokenStore;
        this.signService = signService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
    	// "페이지 이동"에서만 자동갱신
        if (!"GET".equalsIgnoreCase(req.getMethod())) return true;

        String uri = req.getRequestURI();
        String cpath = req.getContextPath();
        String path = (cpath != null && !cpath.isEmpty()) ? uri.substring(cpath.length()) : uri;

        if (path.startsWith("/resources/")) return true;
        if (path.startsWith("/ws-stomp/")) return true;
        if (path.startsWith("/auth/")) return true;
        if (path.startsWith("/api/")) return true;

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // 이미 인증된 요청이면 통과
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(req, res);
            return;
        }

        // AT가 이미 유효하면 -> JwtAuthFilter가 처리하게 통과
        String at = TokenResolver.resolveAccessToken(req);
        if (at != null && jwtProvider.isValid(at)) {
            chain.doFilter(req, res);
            return;
        }


        // AT 없거나 만료 -> RT로 갱신 시도
        String rt = CookieUtil.getCookieValue(req, "RT");
        if (rt == null || rt.isBlank() || !jwtProvider.isValid(rt)) {
            chain.doFilter(req, res);
            return;
        }

        try {
            Claims claims = jwtProvider.parseClaims(rt);
            long userId = Long.parseLong(claims.getSubject());

            String saved = refreshTokenStore.get(userId);
            if (saved == null || !saved.equals(rt)) {
                CookieUtil.deleteCookie(res, "RT");
                CookieUtil.deleteCookie(res, "AT");
                SecurityContextHolder.clearContext();
                chain.doFilter(req, res);
                return;
            }

            // RT가 유효/일치 -> 토큰 재발급
            TokenPair tokens = signService.issueTokensByUserId(userId);

            // 쿠키 갱신(AT/RT)
            CookieUtil.addHttpOnlyCookie(res, "RT", tokens.getRefreshToken(), tokens.getRefreshTtlSeconds());
            CookieUtil.addHttpOnlyCookie(res, "AT", tokens.getAccessToken(), tokens.getAccessTtlSeconds());
            
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(req, res);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            chain.doFilter(req, res);
        }
    }
}
