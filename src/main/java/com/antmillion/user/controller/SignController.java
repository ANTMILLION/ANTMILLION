package com.antmillion.user.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.antmillion.auth.dto.SignUpRequest;
import com.antmillion.auth.jwt.CookieUtil;
import com.antmillion.auth.jwt.JwtProvider;
import com.antmillion.auth.service.SignService;
import com.antmillion.auth.service.SignService.SignUpResult;
import com.antmillion.auth.service.SignService.TokenPair;
import com.antmillion.auth.token.RefreshTokenStore;

import io.jsonwebtoken.Claims;

@Controller
@RequestMapping
public class SignController {

	private static final String SIGNUP_SESSION_KEY = "signupForm";

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
		    ra.addFlashAttribute("email", email);
		    return "redirect:/login";
		}
	}

	// 회원가입 1단계 화면
	@GetMapping("/signup")
	public String signupForm(Model model, HttpSession session) {
		SignUpRequest saved = (SignUpRequest) session.getAttribute(SIGNUP_SESSION_KEY);
		model.addAttribute("form", saved != null ? saved : new SignUpRequest());
		return "login/signup";
	}

	// 회원가입 1단계 처리 -> 세션에 저장 후 step2로 이동
	@PostMapping("/signup")
	public String signupStep1Submit(@ModelAttribute("form") SignUpRequest form,
			@RequestParam("passwordConfirm") String passwordConfirm, HttpSession session, Model model) {
		try {
			String email = (form.getEmail() == null) ? "" : form.getEmail().trim();
			String password = (form.getPassword() == null) ? "" : form.getPassword().trim();

			if (email.isEmpty()) {
				throw new IllegalStateException("이메일을 입력하세요.");
			}
			if (password.isEmpty()) {
				throw new IllegalStateException("비밀번호를 입력하세요.");
			}
			if (!password.equals(passwordConfirm)) {
				throw new IllegalStateException("비밀번호 확인이 일치하지 않습니다.");
			}

			if (!signService.isEmailAvailable(email)) {
				throw new IllegalStateException("이미 사용 중인 이메일입니다.");
			}

			// (기존 유지) 세션에 step1 정보 저장
			SignUpRequest sessionForm = new SignUpRequest();
			sessionForm.setEmail(email);
			sessionForm.setPassword(password);

			session.setAttribute(SIGNUP_SESSION_KEY, sessionForm);

			return "redirect:/signup/step2";

		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("form", form);
			return "login/signup";
		}
	}

	@GetMapping("/signup/check-email")
	@ResponseBody
	public Map<String, Object> checkEmail(@RequestParam("email") String email) {
		if (email == null || email.trim().isEmpty()) {
			return Map.of("available", false, "message", "이메일을 입력하세요.");
		}
		boolean ok = signService.isEmailAvailable(email.trim());
		return Map.of("available", ok, "message", ok ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.");
	}

	@GetMapping("/signup/step2")
	public String signupStep2Form(HttpSession session, RedirectAttributes ra, Model model) {
		SignUpRequest sessionForm = (SignUpRequest) session.getAttribute(SIGNUP_SESSION_KEY);
		if (sessionForm == null) {
			ra.addFlashAttribute("error", "이메일 입력부터 다시 진행하세요.");
			return "redirect:/signup";
		}
		return "login/signup_step2";
	}

	// 회원가입 2단계 처리 -> DB insert + 완료 페이지로
	@PostMapping("/signup/step2")
	public String signupStep2Submit(@RequestParam("nickname") String nickname,
			@RequestParam(value = "agreeTerms", required = false) String agreeTerms,
			@RequestParam(value = "agreePrivacy", required = false) String agreePrivacy, HttpSession session,
			Model model, RedirectAttributes ra) {
		try {
			SignUpRequest sessionForm = (SignUpRequest) session.getAttribute(SIGNUP_SESSION_KEY);
			if (sessionForm == null)
				return "redirect:/signup";

			// step2 제출 직전에 이메일 중복 재검사 (우회/레이스 방지)
			String email = sessionForm.getEmail();
			if (email == null || email.trim().isEmpty()) {
				session.removeAttribute(SIGNUP_SESSION_KEY);
				return "redirect:/signup";
			}
			if (!signService.isEmailAvailable(email.trim())) {
				// 세션을 지워서 step2 반복 진입도 막기
				session.removeAttribute(SIGNUP_SESSION_KEY);
				throw new IllegalStateException("이미 사용 중인 이메일입니다. 처음부터 다시 진행하세요.");
			}

			if (nickname == null || nickname.trim().isEmpty()) {
				throw new IllegalStateException("닉네임을 입력하세요.");
			}
			if (!"Y".equals(agreeTerms) || !"Y".equals(agreePrivacy)) {
				throw new IllegalStateException("필수 약관에 동의해야 합니다.");
			}

			sessionForm.setNickname(nickname);

			SignUpResult result = signService.signUpLocal(sessionForm);

			// step 완료 후 세션 제거
			session.removeAttribute(SIGNUP_SESSION_KEY);

			ra.addFlashAttribute("accountNumber", result.getAccountNumber());
			ra.addFlashAttribute("balance", result.getBalance());

			return "redirect:/signup/complete";
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("nickname", nickname);
			return "login/signup_step2";
		}
	}

	@GetMapping("/signup/check-nickname")
	@ResponseBody
	public Map<String, Object> checkNickname(@RequestParam("nickname") String nickname) {
		if (nickname == null || nickname.trim().isEmpty()) {
			return Map.of("available", false, "message", "닉네임을 입력하세요.");
		}
		boolean ok = signService.isNicknameAvailable(nickname.trim());
		return Map.of("available", ok, "message", ok ? "사용 가능한 닉네임입니다." : "이미 사용 중인 닉네임입니다.");
	}

	@GetMapping("/signup/complete")
	public String signupComplete() {
		return "login/signup_complete";
	}

	@PostMapping("/signup/complete")
	public String signupCompleteSubmit() {
		return "redirect:/";
	}
}