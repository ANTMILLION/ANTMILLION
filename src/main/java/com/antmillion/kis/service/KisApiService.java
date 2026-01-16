package com.antmillion.kis.service;

import com.antmillion.kis.config.KisConfig;
import com.antmillion.kis.dto.KisAccessTokenRequest;
import com.antmillion.kis.dto.KisAccessTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
public class KisApiService {

    private final KisConfig config;
    private final RestTemplate restTemplate;

    public KisAccessTokenResponse issueAccessToken() {

        String url = config.baseUrl + "/oauth2/tokenP";
        KisAccessTokenRequest body = new KisAccessTokenRequest();
        body.setAppkey(config.appKey);
        body.setAppsecret(config.appSecret);
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<KisAccessTokenRequest> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<KisAccessTokenResponse> response = restTemplate.postForEntity(url, request, KisAccessTokenResponse.class);
        return response.getBody();
    }

}
