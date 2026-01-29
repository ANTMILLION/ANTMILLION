package com.antmillion.auth.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

	@PostMapping("/refresh")
	public ResponseEntity<Map<String, Object>> refresh(HttpServletRequest req, HttpServletResponse res) {
		String rt = CookieUtil.getCookieValue(req, "RT");
		if (rt == null || rt.isBlank() || !jwtProvider.isValid(rt)) {
			CookieUtil.deleteCookie(res, "RT");
			CookieUtil.deleteCookie(res, "AT");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("ok", false, "message", "NO_REFRESH"));
		}

		Claims claims = jwtProvider.parseClaims(rt);
		long userId = Long.parseLong(claims.getSubject());

		String saved = refreshTokenStore.get(userId);
		if (saved == null || !saved.equals(rt)) {
			CookieUtil.deleteCookie(res, "RT");
			CookieUtil.deleteCookie(res, "AT");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("ok", false, "message", "REFRESH_MISMATCH"));
		}

		TokenPair tokens = signService.issueTokensByUserId(userId);

		// RT/AT 둘 다 쿠키로 갱신
		CookieUtil.addHttpOnlyCookie(res, "RT", tokens.getRefreshToken(), tokens.getRefreshTtlSeconds());
		CookieUtil.addHttpOnlyCookie(res, "AT", tokens.getAccessToken(), tokens.getAccessTtlSeconds());

		return ResponseEntity.ok().body(Map.of("ok", true));
	}
}
