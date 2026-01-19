package com.antmillion.kis.service;

import com.antmillion.kis.config.KisConfig;
import com.antmillion.kis.constant.KisApiConstant;
import com.antmillion.kis.dto.*;
import com.antmillion.kis.repository.KisAccessTokenRedisRepository;
import com.antmillion.kis.repository.KisChartRedisRepository;
import com.antmillion.kis.repository.KisMarketIndexChartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class KisApiService {

    private final KisConfig config;
    private final RestTemplate restTemplate;
    private final KisAccessTokenRedisRepository kisAccessTokenRedisRepository;
    private final KisChartRedisRepository kisChartRedisRepository;
    private final KisMarketIndexChartRepository kisMarketIndexChartRepository;

    /**
     * KIS 액세스 토큰 조회/발급
     * Redis에 유효한 토큰이 있으면 반환, 없으면 신규 발급 후 Redis에 저장
     */
    public String getKisAccessToken() {
        // Redis에서 토큰 조회
        Optional<String> saved = kisAccessTokenRedisRepository.findAccessToken();
        if (saved.isPresent()) {
            log.info("기존 토큰 재사용");
            return saved.get();
        }

        // 신규 발급
        log.info("토큰 신규 발급");
        KisAccessTokenResponse response = issueAccessTokenAPI();

        // Redis 저장 (TTL 자동 설정)
        kisAccessTokenRedisRepository.save(
                response.getAccessToken(),
                response.getExpiresIn()
        );

        return response.getAccessToken();
    }

    /**
     * 신규 접근 토큰 발급
     * 24시간 유효, 6시간마다 재발급 가능, api 호출 시 1분간 재호출 제한
     * @return 접근 토큰, 유효시간
     */
    private KisAccessTokenResponse issueAccessTokenAPI() {
        String url = config.getBaseUrl() + KisApiConstant.OAUTH_TOKEN_PATH;
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
    public List<ChartStockPrice> getPeriodStockPrices(ChartStockPriceRequest request) {
        //Redis에 있는지 먼저 확인
        Optional<List<ChartStockPrice>> cached = kisChartRedisRepository.getChartData(
                request.getStockCode(),
                request.getPeriodCode()
        );

        if (cached.isPresent()) {
            log.info("기간별 차트 데이터 재사용");
            return cached.get();
        }

        log.info("한국투자증권 기간별 시세 api 호출");
        KisChartStockPriceResponse response = periodStockPricesAPI(request);
        kisChartRedisRepository.save(request.getStockCode(), request.getPeriodCode(), response.getOutput2());
        return response.getOutput2();
    }

    /**
     * 한국투자증권 국내주식 기간별 시세 조회 API 호출
     * @param request  조회 조건 (종목코드, 기간, 날짜 등)
     * @return 한국투자증권 API 반환값
     */
    private KisChartStockPriceResponse periodStockPricesAPI(ChartStockPriceRequest request) {
        //1.접근 토큰 얻기
        String token = getKisAccessToken();
        //2.헤더 설정
        HttpHeaders headers = createApiHeader(token, "FHKST03010100");
        //3.URL 생성
        String url = buildPeriodApiUrl(request);
        //4.API 호출
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<KisChartStockPriceResponse> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, KisChartStockPriceResponse.class);
        //5.응답 처리
        KisChartStockPriceResponse responseBody = response.getBody();
        if (responseBody != null && responseBody.getOutput2() != null) {
            return responseBody;
        }
        throw new RuntimeException("기간별 차트 데이터 조회 실패");
    }

    /**
     * 한국투자증권 국내 주식 기간별 시세 조회 API URL 생성
     * @param request  조회 조건 (종목코드, 기간, 날짜 등)
     * @return 한국투자증권 API URL
     */
    private String buildPeriodApiUrl(ChartStockPriceRequest request) {
        final URI uri = URI.create(config.getBaseUrl() + KisApiConstant.PERIOD_PRICE_PATH);
        return UriComponentsBuilder
                .fromUri(uri)
                .queryParam("FID_COND_MRKT_DIV_CODE", request.getMarketCode())
                .queryParam("FID_INPUT_ISCD", request.getStockCode())
                .queryParam("FID_INPUT_DATE_1", request.getStartDate())
                .queryParam("FID_INPUT_DATE_2", request.getEndDate())
                .queryParam("FID_PERIOD_DIV_CODE", request.getPeriodCode())
                .queryParam("FID_ORG_ADJ_PRC", request.getAdjPrice())
                .build().toUriString();
    }

    /**
     * 한국투자증권 API 호출을 위한 헤더 생성
     * @param token 접근 토큰
     * @param trId 거래 id
     * @return 캔들 차트 데이터 리스트
     */
    private HttpHeaders createApiHeader(String token, String trId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", "application/json; charset=utf-8");
        headers.setBearerAuth(token);
        headers.set("appkey", config.appKey);
        headers.set("appsecret", config.appSecret);
        headers.set("tr_id", trId);
        headers.set("custtype", "P");
        return headers;

    }

    /**
     * 한국투자증권 API 호출을 위한 헤더 생성
     * @param request 조회 조건 (종목코드, 기간, 날짜 등)
     * @return 코스피, 코스닥 캔들 차트 데이터 리스트
     */
    public List<MarketIndexPrice> getMarketIndexPrices(MarketIndexPriceRequest request) {
        Optional<List<MarketIndexPrice>> cached = kisMarketIndexChartRepository.getChartData(request.getIndexCode());
        if (cached.isPresent()) {
            log.info("{} 차트 데이터 재사용",  request.getIndexCode());
            return cached.get();
        }

        log.info("한국투자증권 국내업종 일자별지수 api 호출");
        KisMarketIndexPriceResponse response = marketIndexPricesAPI(request);
        kisMarketIndexChartRepository.save(request.getIndexCode(), response.getOutput2());
        return response.getOutput2();
    }

    /**
     * 한국투자증권 국내업종 일자별지수 API 호출
     * @param request 조회 조건 (종목코드, 기간, 날짜 등)
     * @return 코스피, 코스닥 캔들 차트 데이터 리스트
     */
    private KisMarketIndexPriceResponse marketIndexPricesAPI(MarketIndexPriceRequest request) {
        String token = getKisAccessToken();
        HttpHeaders headers = createApiHeader(token, "FHPUP02120000");
        String url = buildMarketIndexApiUrl(request);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<KisMarketIndexPriceResponse> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, KisMarketIndexPriceResponse.class);
        KisMarketIndexPriceResponse responseBody = response.getBody();
        if (responseBody != null && responseBody.getOutput2() != null) {
            return responseBody;
        }
        throw new RuntimeException("국내 시장 지수 차트 데이터 조회 실패");
    }

    /**
     * 한국투자증권 국내업종 일자별지수 API URL 생성
     * @param request 조회 조건 (종목코드, 기간, 날짜 등)
     * @return 한국투자증권 국내업종 일자별지수 API URL
     */
    private String buildMarketIndexApiUrl(MarketIndexPriceRequest request) {
        final URI uri = URI.create(config.getBaseUrl() + KisApiConstant.MARKET_INDEX_PATH);
        return UriComponentsBuilder
                .fromUri(uri)
                .queryParam("FID_PERIOD_DIV_CODE", request.getPeriodCode())
                .queryParam("FID_COND_MRKT_DIV_CODE", request.getMarketCode())
                .queryParam("FID_INPUT_ISCD", request.getIndexCode())
                .queryParam("FID_INPUT_DATE_1", request.getEndDate())
                .build().toUriString();
    }

}
