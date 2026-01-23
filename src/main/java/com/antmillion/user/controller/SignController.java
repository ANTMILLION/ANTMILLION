package com.antmillion.user.controller;

import java.util.Map;
import java.util.regex.Pattern;

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
import com.antmillion.auth.terms.TermsProvider;
import com.antmillion.auth.token.RefreshTokenStore;
import com.antmillion.kakao.token.KakaoSignupStore;

@Controller
@RequestMapping
public class SignController {

	private static final String SIGNUP_SESSION_KEY = "signupForm";
	private static final String KAKAO_SIGNUP_KEY = "KAKAO_SIGNUP_KEY";

	private final TermsProvider termsProvider;
	private final SignService signService;
	private final KakaoSignupStore kakaoSignupStore;
	private static final Pattern EMAIL_RULE = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
	private static final Pattern PW_RULE = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");

	public SignController(SignService signService, JwtProvider jwtProvider, RefreshTokenStore refreshTokenStore,
			TermsProvider termsProvider, KakaoSignupStore kakaoSignupStore) {
		this.termsProvider = termsProvider;
		this.signService = signService;
		this.kakaoSignupStore = kakaoSignupStore;
	}

	// 로그인 화면
	@GetMapping("/login")
	public String loginForm() {
		return "login/login";
	}

	@PostMapping("/login")
	public String loginSubmit(@RequestParam("email") String email, @RequestParam("password") String password,
			HttpServletResponse response, RedirectAttributes ra) {
		email = (email == null) ? "" : email.trim();
		
		if (email.isEmpty() || !EMAIL_RULE.matcher(email).matches()) {
			ra.addFlashAttribute("loginError", "이메일 형식이 올바르지 않습니다.");
			ra.addFlashAttribute("email", email);
			return "redirect:/login";
		}
		if (password == null || !PW_RULE.matcher(password).matches()) {
			ra.addFlashAttribute("loginError", "비밀번호는 영문과 숫자를 포함해 8자리 이상이어야 합니다.");
			ra.addFlashAttribute("email", email);
			return "redirect:/login";
		}
		try {
			TokenPair tokens = signService.login(email, password);

			// RT는 HttpOnly 쿠키로만 저장
			CookieUtil.addHttpOnlyCookie(response, "RT", tokens.getRefreshToken(), tokens.getRefreshTtlSeconds());
			// AT는 쿠키로 저장하지 않는다.
			// (화면 진입 후 JS가 /auth/refresh 를 호출해서 AT를 헤더/스토리지로 보관)

			return "redirect:/";
		} catch (Exception e) {
			ra.addFlashAttribute("loginError", "이메일/비밀번호를 확인해 주세요.");
			ra.addFlashAttribute("email", email);
			return "redirect:/login";
		}
	}

	// 회원가입 1단계 화면
	@GetMapping("/signup")
	public String signupForm(Model model, HttpSession session) {
		// 로컬 회원가입 화면 들어오면 카카오 가입 시도는 취소로 간주하고 정리
		String kakaoKey = (String) session.getAttribute(KAKAO_SIGNUP_KEY);
		if (kakaoKey != null) {
			kakaoSignupStore.delete(kakaoKey);
			session.removeAttribute(KAKAO_SIGNUP_KEY);
		}

		SignUpRequest saved = (SignUpRequest) session.getAttribute(SIGNUP_SESSION_KEY);
		model.addAttribute("form", saved != null ? saved : new SignUpRequest());
		return "login/signup";
	}

	// 회원가입 1단계 처리 -> 세션에 저장 후 step2로 이동
	@PostMapping("/signup")
	public String signupStep1Submit(@ModelAttribute("form") SignUpRequest form,
			@RequestParam("passwordConfirm") String passwordConfirm, HttpSession session, Model model) {
		// 로컬 회원가입 시작 = 카카오 가입 진행중이면 취소 처리
		String kakaoKey = (String) session.getAttribute(KAKAO_SIGNUP_KEY);
		if (kakaoKey != null) {
			kakaoSignupStore.delete(kakaoKey);
			session.removeAttribute(KAKAO_SIGNUP_KEY);
		}
		String email = (form.getEmail() == null) ? "" : form.getEmail().trim();
		String password = (form.getPassword() == null) ? "" : form.getPassword().trim();

		// 이메일 형식
		if (email.isEmpty() || !EMAIL_RULE.matcher(email).matches()) {
			model.addAttribute("error", "이메일 형식이 올바르지 않습니다.");
			model.addAttribute("form", form);
			return "login/signup";
		}

		// 비밀번호 규칙
		if (password.isEmpty()) {
			model.addAttribute("error", "비밀번호를 입력해 주세요.");
			model.addAttribute("form", form);
			return "login/signup";
		}
		if (password == null || password.isEmpty()) {
			model.addAttribute("signupError", "비밀번호를 입력해 주세요.");
			return "signup/signup";
		}

		if (!PW_RULE.matcher(password).matches()) {
			model.addAttribute("signupError", "비밀번호는 영문과 숫자를 포함해 8자리 이상이어야 합니다.");
			return "signup/signup";
		}

		if (!password.equals(passwordConfirm)) {
			model.addAttribute("signupError", "비밀번호가 일치하지 않습니다.");
			return "signup/signup";
		}
		try {

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
		email = (email == null) ? "" : email.trim();
		if (email.isEmpty()) {
		    return Map.of("available", false, "message", "이메일을 입력하세요.");
		}
		if (!EMAIL_RULE.matcher(email).matches()) {
		    return Map.of("available", false, "message", "이메일 형식이 올바르지 않습니다.");
		}
		boolean ok = signService.isEmailAvailable(email);
		return Map.of("available", ok, "message", ok ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.");
	}

	@GetMapping("/signup/step2")
	public String signupStep2Form(HttpSession session, RedirectAttributes ra, Model model) {
		SignUpRequest sessionForm = (SignUpRequest) session.getAttribute(SIGNUP_SESSION_KEY);
		if (sessionForm == null) {
			ra.addFlashAttribute("error", "이메일이나 비밀번호 입력이 잘못되었습니다.");
			return "redirect:/signup";
		}

		model.addAttribute("termsText", termsProvider.getServiceTerms());
		model.addAttribute("privacyText", termsProvider.getPrivacyTerms());
		return "login/signup_step2";
	}

	// 회원가입 2단계 처리 -> DB insert + 완료 페이지로
	@PostMapping("/signup/step2")
	public String signupStep2Submit(@RequestParam("nickname") String nickname,
			@RequestParam(value = "agreeTerms", required = false) String agreeTerms,
			@RequestParam(value = "agreePrivacy", required = false) String agreePrivacy, HttpSession session,
			Model model, RedirectAttributes ra, HttpServletResponse response) {
		try {
			SignUpRequest sessionForm = (SignUpRequest) session.getAttribute(SIGNUP_SESSION_KEY);
			if (sessionForm == null)
				return "redirect:/signup";

			// 카카오 가입 플래그 (세션에 있으면 카카오 플로우)
			String kakaoSignupKey = (String) session.getAttribute(KAKAO_SIGNUP_KEY);

			// 로컬 우선: sessionForm에 email이 있으면 로컬로 본다
			String emailInForm = sessionForm.getEmail();
			boolean hasLocalEmail = (emailInForm != null && !emailInForm.trim().isEmpty());

			boolean isKakao = (kakaoSignupKey != null) && !hasLocalEmail;

			// 로컬일 때만: 이메일 중복 재검사
			if (!isKakao) {
				String email = sessionForm.getEmail();
				if (email == null || email.trim().isEmpty()) {
					session.removeAttribute(SIGNUP_SESSION_KEY);
					return "redirect:/signup";
				}
				if (!signService.isEmailAvailable(email.trim())) {
					session.removeAttribute(SIGNUP_SESSION_KEY);
					throw new IllegalStateException("이미 사용 중인 이메일입니다. 처음부터 다시 진행하세요.");
				}
			}

			// 공통: 닉네임/약관 체크
			if (nickname == null || nickname.trim().isEmpty()) {
				throw new IllegalStateException("닉네임을 입력하세요.");
			}
			if (!signService.isNicknameAvailable(nickname.trim())) {
				throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
			}
			if (!"Y".equals(agreeTerms) || !"Y".equals(agreePrivacy)) {
				throw new IllegalStateException("필수 약관에 동의해야 합니다.");
			}

			// 카카오면: Redis에서 kakaoId 꺼내서 카카오 회원가입 처리
			if (isKakao) {
				Long kakaoId = kakaoSignupStore.get(kakaoSignupKey);
				if (kakaoId == null) {
					session.removeAttribute(KAKAO_SIGNUP_KEY);
					session.removeAttribute(SIGNUP_SESSION_KEY);
					throw new IllegalStateException("카카오 가입 시간이 만료되었습니다. 다시 카카오 로그인을 진행하세요.");
				}

				// DB insert: member(email/password null) + account + social
				SignUpResult result = signService.signUpKakao(kakaoId, nickname.trim());

				// 가입과 동시에 로그인(토큰 발급 + 쿠키 세팅)
				TokenPair tokens = signService.issueTokensByUserId(result.getUserId());
				CookieUtil.addHttpOnlyCookie(response, "RT", tokens.getRefreshToken(), tokens.getRefreshTtlSeconds());
				// AT는 쿠키로 저장하지 않는다.

				// 임시 데이터 정리
				kakaoSignupStore.delete(kakaoSignupKey);
				session.removeAttribute(KAKAO_SIGNUP_KEY);
				session.removeAttribute(SIGNUP_SESSION_KEY);

				ra.addFlashAttribute("signupType", "KAKAO");
				ra.addFlashAttribute("accountNumber", result.getAccountNumber());
				ra.addFlashAttribute("balance", result.getBalance());
				return "redirect:/signup/complete";
			}
			sessionForm.setNickname(nickname);

			SignUpResult result = signService.signUpLocal(sessionForm);

			// 혹시 남아있는 카카오 진행 흔적이 있으면 정리
			String kakaoKeyLeft = (String) session.getAttribute(KAKAO_SIGNUP_KEY);
			if (kakaoKeyLeft != null) {
				kakaoSignupStore.delete(kakaoKeyLeft);
				session.removeAttribute(KAKAO_SIGNUP_KEY);
			}

			// step 완료 후 세션 제거
			session.removeAttribute(SIGNUP_SESSION_KEY);

			ra.addFlashAttribute("signupType", "LOCAL");
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