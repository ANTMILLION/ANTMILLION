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

import com.antmillion.auth.jwt.CookieUtil;

@Controller
@RequestMapping("/kakao")
public class KakaoController {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Autowired
    private com.antmillion.kakao.service.KakaoService kakaoService;
    
    // 선택: 이메일/프로필 등 동의항목을 추가로 강제하고 싶을 때
    // (처음부터 콘솔에서 동의항목 설정해두면 scope 없이도 동작하지만,
    //  추가동의 요청/명시가 필요하면 사용)
    private static final String SCOPE = "profile_nickname";

    @GetMapping("/login")
    public String kakaoLogin(HttpServletResponse response) {
        String state = UUID.randomUUID().toString();
        CookieUtil.addHttpOnlyCookie(response, "KAKAO_STATE", state, 300); // 5분

        String url = "https://kauth.kakao.com/oauth/authorize"
                + "?response_type=code"
                + "&client_id=" + enc(clientId)
                + "&redirect_uri=" + enc(redirectUri)
                + "&state=" + enc(state)
                + "&scope=" + enc(SCOPE);

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

        // 4) 여기서부터: code -> token -> user/me
        // (아래 KakaoOAuthService 만들고 주입받아 호출)
        return "redirect:/";
    }
}
