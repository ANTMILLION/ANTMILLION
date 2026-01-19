package com.antmillion.user.controller;

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
    public String loginSubmit(@RequestParam("email") String email,
                              @RequestParam("password") String password,
                              HttpServletResponse response,
                              RedirectAttributes ra) {
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
    public String signupForm(Model model, HttpSession session) {
        SignUpRequest saved = (SignUpRequest) session.getAttribute(SIGNUP_SESSION_KEY);
        model.addAttribute("form", saved != null ? saved : new SignUpRequest());
        return "login/signup";
    }

    // 회원가입 1단계 처리 -> 세션에 저장 후 step2로 이동
    @PostMapping("/signup")
    public String signupStep1Submit(@ModelAttribute("form") SignUpRequest form,
                                    @RequestParam("passwordConfirm") String passwordConfirm,
                                    HttpSession session,
                                    Model model) {
        try {
            if (form.getEmail() == null || form.getEmail().trim().isEmpty()) {
                throw new IllegalStateException("이메일을 입력하세요.");
            }
            if (form.getPassword() == null || form.getPassword().trim().isEmpty()) {
                throw new IllegalStateException("비밀번호를 입력하세요.");
            }
            if (!form.getPassword().equals(passwordConfirm)) {
                throw new IllegalStateException("비밀번호 확인이 일치하지 않습니다.");
            }

            SignUpRequest sessionForm = new SignUpRequest();
            sessionForm.setEmail(form.getEmail());
            sessionForm.setPassword(form.getPassword());
            
            session.setAttribute(SIGNUP_SESSION_KEY, sessionForm);

            return "redirect:/signup/step2";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "login/signup";
        }
    }

    @GetMapping("/signup/step2")
    public String signupStep2Form(HttpSession session) {
        if (session.getAttribute(SIGNUP_SESSION_KEY) == null) {
            return "redirect:/signup";
        }
        return "login/signup_step2";
    }

    // 회원가입 2단계 처리 -> DB insert + 완료 페이지로
    @PostMapping("/signup/step2")
    public String signupStep2Submit(@RequestParam("nickname") String nickname,
                                    @RequestParam(value = "agreeTerms", required = false) String agreeTerms,
                                    @RequestParam(value = "agreePrivacy", required = false) String agreePrivacy,
                                    HttpSession session,
                                    Model model,
                                    RedirectAttributes ra) {
        try {
            SignUpRequest sessionForm = (SignUpRequest) session.getAttribute(SIGNUP_SESSION_KEY);
            if (sessionForm == null) return "redirect:/signup";

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

            // 완료 페이지에 보여줄 값 전달 (redirect여도 flash로 전달 가능)
            ra.addFlashAttribute("accountNumber", result.getAccountNumber());
            ra.addFlashAttribute("balance", result.getBalance());

            return "redirect:/signup/complete";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("nickname", nickname);
            return "login/signup_step2";
        }
    }
    
    @GetMapping("/signup/complete")
    public String signupComplete() {
        return "login/signup_complete";
    }

    @PostMapping("/signup/complete")
    public String signupCompleteSubmit() {
        return "redirect:/";
    }

    // 로그아웃
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
