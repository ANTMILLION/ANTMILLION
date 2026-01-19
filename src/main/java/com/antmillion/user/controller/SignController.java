package com.antmillion.user.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.antmillion.auth.jwt.CookieUtil;
import com.antmillion.auth.jwt.JwtProvider;
import com.antmillion.auth.service.SignService;
import com.antmillion.auth.service.SignService.TokenPair;
import com.antmillion.auth.token.RefreshTokenStore;

import io.jsonwebtoken.Claims;

@Controller
@RequestMapping

public class SignController {

	private final SignService signService;
	private final JwtProvider jwtProvider;
	private final RefreshTokenStore refreshTokenStore;

	public SignController(SignService signService, JwtProvider jwtProvider, RefreshTokenStore refreshTokenStore) {
		this.signService = signService;
		this.jwtProvider = jwtProvider;
		this.refreshTokenStore = refreshTokenStore;
	}

	// 로그인 화면
	@GetMapping("/login")
	public String loginForm() {
		return "login/login";
	}

	@PostMapping("/login")
	public String loginSubmit(@RequestParam("email") String email, @RequestParam("password") String password,
			HttpServletResponse response, RedirectAttributes ra) {
		try {
			TokenPair tokens = signService.login(email, password);

			CookieUtil.addHttpOnlyCookie(response, "AT", tokens.getAccessToken(), tokens.getAccessTtlSeconds());
			CookieUtil.addHttpOnlyCookie(response, "RT", tokens.getRefreshToken(), tokens.getRefreshTtlSeconds());

			return "redirect:/";
		} catch (Exception e) {
			ra.addFlashAttribute("loginError", "아이디/비밀번호를 확인해 주세요.");
			return "redirect:/login";
		}
	}

	// 회원가입 1단계 화면
	@GetMapping("/signup")
	public String signupForm() {
		return "login/signup";
	}

	// 회원가입 1단계 처리 -> 2단계로
	@PostMapping("/signup")
	public String signupSubmit() {
		return "redirect:/signup/step2";
	}

	// 회원가입 2단계 화면
	@GetMapping("/signup/step2")
	public String signupStep2Form() {
		return "login/signup_step2";
	}

	// 회원가입 2단계 처리 -> 완료로
	@PostMapping("/signup/step2")
	public String signupStep2Submit() {
		return "redirect:/signup/complete";
	}

	// 회원가입 완료 화면
	@GetMapping("/signup/complete")
	public String signupCompleteForm() {
		return "login/signup_complete";
	}

	// 회원가입 완료 화면 처리 -> 메인으로
	@PostMapping("/signup/complete")
	public String signupComplete_Submit() {
		return "redirect:/";
	}

	@PostMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		String rt = CookieUtil.getCookieValue(request, "RT");
		if (rt != null && jwtProvider.isValid(rt)) {
			Claims claims = jwtProvider.parseClaims(rt);
			long userId = Long.parseLong(claims.getSubject());
			refreshTokenStore.delete(userId);
		}
		
		CookieUtil.deleteCookie(response, "AT");
		CookieUtil.deleteCookie(response, "RT");

		return "redirect:/login";
	}
}