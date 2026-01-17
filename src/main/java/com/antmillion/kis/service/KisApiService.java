package com.antmillion.kis.service;

import com.antmillion.kis.config.KisConfig;
import com.antmillion.kis.dto.ChartStockPriceRequest;
import com.antmillion.kis.dto.KisAccessTokenRequest;
import com.antmillion.kis.dto.KisAccessTokenResponse;
import com.antmillion.kis.repository.KisAccessTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class KisApiService {

    private final KisConfig config;
    private final RestTemplate restTemplate;
    private final KisAccessTokenRedisRepository kisAccessTokenRedisRepository;

    /**
     * KIS 액세스 토큰 조회/발급
     * Redis에 유효한 토큰이 있으면 반환, 없으면 신규 발급 후 Redis에 저장
     */
    public String getKisAccessToken() {
        // Redis에서 토큰 조회
        Optional<String> saved = kisAccessTokenRedisRepository.findAccessToken();
        if (saved.isPresent()) {
            return saved.get();
        }

        // 신규 발급
        KisAccessTokenResponse response = issueAccessToken();

        // Redis 저장 (TTL 자동 설정)
        kisAccessTokenRedisRepository.save(
                response.getAccess_token(),
                response.getExpires_in()
        );

        return response.getAccess_token();
    }

    /**
     * 신규 접근 토큰 발급
     * 24시간 유효, 6시간마다 재발급 가능, api 호출 시 1분간 재호출 제한
     * @return 접근 토큰, 유효시간
     */
    private KisAccessTokenResponse issueAccessToken() {

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

    /**
     * 국내주식 기간별 시세 조회
     * @param request  조회 조건 (종목코드, 기간, 날짜 등)
     * @return 캔들 차트 데이터 리스트
     */
    public List<ChartStockPriceRequest> getPeriodStockPrices(ChartStockPriceRequest request) {
        String token = getKisAccessToken();
        HttpHeaders headers = createPeriodApiHeader(token);
        String url = buildPeriodApiUrl();
        return null;
    }

    private String buildPeriodApiUrl() {
    }

    private HttpHeaders createPeriodApiHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", "application/json; charset=utf-8");
        headers.setBearerAuth(token);
        headers.set("appkey", config.appKey);
        headers.set("appsecret", config.appSecret);
        headers.set("tr_id", "FHKST03010100");  // 국내주식 기간별시세 TR_ID
        headers.set("custtype", "P");
        return headers;

    }

}
