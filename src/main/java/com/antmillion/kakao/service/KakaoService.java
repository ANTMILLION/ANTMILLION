package com.antmillion.kakao.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.antmillion.kakao.dto.KakaoTokenResponse;
import com.antmillion.kakao.dto.KakaoUserResponse;

@Service
public class KakaoService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret:}")
    private String clientSecret;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    public KakaoTokenResponse exchangeToken(String code) {
        String url = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        // client_secret 사용 설정했으면 같이 전송
        if (clientSecret != null && !clientSecret.isBlank()) {
            body.add("client_secret", clientSecret);
        }

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<KakaoTokenResponse> resp =
                restTemplate.exchange(url, HttpMethod.POST, entity, KakaoTokenResponse.class);

        return resp.getBody();
    }

    public KakaoUserResponse getUserInfo(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserResponse> resp =
                restTemplate.exchange(url, HttpMethod.GET, entity, KakaoUserResponse.class);

        return resp.getBody();
    }
}