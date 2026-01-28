package com.antmillion.auth.jwt;

import javax.servlet.http.HttpServletRequest;

public class TokenResolver {
	
	private TokenResolver() {}

	public static String resolveBearer(HttpServletRequest req) {
		String bearer = req.getHeader("Authorization");
		if (bearer != null && bearer.startsWith("Bearer ")) {
			String token = bearer.substring(7);
			return (token == null || token.isBlank()) ? null : token;
		}
		return null;
	}

	public static String resolveAccessToken(HttpServletRequest req) {
		String token = resolveBearer(req);
		if (token != null) {
			return token;
		}

		String atCookie = CookieUtil.getCookieValue(req, "AT");
		if (atCookie != null && !atCookie.isBlank()) {
			return atCookie;
		}
		return null;
	}
}
