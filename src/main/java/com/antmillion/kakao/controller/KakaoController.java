package com.antmillion.kakao.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.antmillion.auth.dto.SignUpRequest;
import com.antmillion.auth.jwt.CookieUtil;
import com.antmillion.auth.jwt.JwtProvider;
import com.antmillion.auth.mapper.SocialMapper;
import com.antmillion.auth.service.SignService;
import com.antmillion.auth.terms.TermsProvider;
import com.antmillion.auth.token.RefreshTokenStore;
import com.antmillion.kakao.service.KakaoService;
import com.antmillion.kakao.token.KakaoSignupStore;

@Controller
@RequestMapping("/kakao")
public class KakaoController {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final KakaoService kakaoService;
    private final KakaoSignupStore kakaoSignupStore;
    private final SocialMapper socialMapper;
    private final SignService signService;
    
    public KakaoController(SignService signService, JwtProvider jwtProvider, RefreshTokenStore refreshTokenStore,
			TermsProvider termsProvider, KakaoSignupStore kakaoSignupStore, SocialMapper socialMapper, KakaoService kakaoService) {
		this.kakaoService = kakaoService;
		this.socialMapper = socialMapper;
		this.signService = signService;
		this.kakaoSignupStore = kakaoSignupStore;
	}

    @GetMapping("/login")
    public String kakaoLogin(HttpServletResponse response) {
        String state = UUID.randomUUID().toString();
        CookieUtil.addHttpOnlyCookie(response, "KAKAO_STATE", state, 300); // 5분

        String url = "https://kauth.kakao.com/oauth/authorize"
                + "?response_type=code"
                + "&client_id=" + enc(clientId)
                + "&redirect_uri=" + enc(redirectUri)
                + "&state=" + enc(state);

        return "redirect:" + url;
    }

    private String enc(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }
    @GetMapping("/callback")
    public String kakaoCallback(
            javax.servlet.http.HttpServletRequest request,
            HttpServletResponse response,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String code,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String state,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String error,
            @org.springframework.web.bind.annotation.RequestParam(required = false, name="error_description") String errorDesc
    ) {
        // 1) 카카오에서 에러로 온 경우
        if (error != null) {
            // 필요하면 로그 찍고
            System.out.println("[KAKAO] error=" + error + ", desc=" + errorDesc);
            return "redirect:/login";
        }

        // 2) code 없는 경우 방어
        if (code == null || code.isBlank()) {
            return "redirect:/login";
        }

        // 3) state 검증 (로그인 CSRF 방지)
        String savedState = CookieUtil.getCookieValue(request, "KAKAO_STATE");
        if (savedState == null || state == null || !savedState.equals(state)) {
            // state 불일치면 즉시 차단
            return "redirect:/login";
        }
        // state 1회성: 바로 삭제 권장
        CookieUtil.deleteCookie(response, "KAKAO_STATE");

        // 4) code -> token -> user/me
        try {
        var token = kakaoService.exchangeToken(code);
        if (token == null || token.getAccessToken() == null) {
            return "redirect:/login";
        }

        var user = kakaoService.getUserInfo(token.getAccessToken());
        if (user == null || user.getId() == null) {
            return "redirect:/login";
        }

        long kakaoId = user.getId();

        // 5) 이미 가입된 카카오 유저면 바로 로그인 처리
        Long userId = socialMapper.selectUserIdByKakaoId(kakaoId);
        if (userId != null) {
            var tokens = signService.issueTokensByUserId(userId);
            CookieUtil.addHttpOnlyCookie(response, "RT", tokens.getRefreshToken(), tokens.getRefreshTtlSeconds());
            return "redirect:/";
        }

        // 6) 신규면: Redis에 kakaoId 임시 저장 + 세션에 signupKey 저장 후 step2로
        String signupKey = UUID.randomUUID().toString();
        kakaoSignupStore.save(signupKey, kakaoId, 600); // 10분 TTL

        request.getSession().setAttribute("KAKAO_SIGNUP_KEY", signupKey);

        // step2 진입 통과용(기존 로직 유지하려고 빈 폼 넣기)
        request.getSession().removeAttribute("signupForm");
        request.getSession().setAttribute("signupForm", new SignUpRequest());

        return "redirect:/signup/step2";
        } catch(Exception e) {
        	System.out.println("[KAKAO] callback fail: " + e.getMessage());
        	return "redirect:/login";
        }
    }
}
