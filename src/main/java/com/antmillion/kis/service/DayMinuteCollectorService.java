// 주식당일분봉조회-차트당일분봉
package com.antmillion.kis.service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.antmillion.kis.dto.DayMinuteFlatDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class DayMinuteCollectorService {
	private final DayMinuteRedisChartService redisChartService;
	private final RestTemplate restTemplate;

	// [수집용] 스케줄러 호출
	public void fetchAndSaveMinuteData(String stockCode) {
		try {
			// KIS API 호출 및 Redis 저장

			// 한투 oauth pr 합쳐지면 삭제
			String access_token = "";
			String appkey = "";
			String appsecret = "";

			// 1. 헤더 설정
			HttpHeaders headers = new HttpHeaders();
			headers.set("content-type", "application/json; charset=utf-8");
			headers.set("authorization", "Bearer " + access_token); // 접근 토큰 (Rest API : Access token)
			headers.set("appkey", appkey); // 앱 키
			headers.set("appsecret", appsecret); // 앱 시크릿 키
			headers.set("tr_id", "FHKST03010200"); // 거래 ID (모의 TR ID : FHKST03010200)
			headers.set("custtype", "P"); // 고객 타입 (P : 개인)

			// 2. 시간 및 URL 설정
			String now = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));

			String url = UriComponentsBuilder.fromHttpUrl(
					"https://openapivts.koreainvestment.com:29443/uapi/domestic-stock/v1/quotations/inquire-time-itemchartprice")
					.queryParam("FID_COND_MRKT_DIV_CODE", "J") // 조건 시장 분류 코드(J : KRX)
					.queryParam("FID_INPUT_ISCD", stockCode) // 입력 종목코드
					.queryParam("FID_INPUT_HOUR_1", now) // 입력 시간(현재 시간, 1분봉)
					.queryParam("FID_PW_DATA_INCU_YN", "Y") // 과거 데이터 포함 여부(Y : 최근 30개 output2 받을 수 있음)
					.queryParam("FID_ETC_CLS_CODE", "") // 기타 구분 코드(NULL)
					.encode().toUriString();

			// 3. API 호출
			// Header와 Body를 묶어서 한투 서버에 전달함. GET 방식이라 보낼 바디 내용이 없어서 String 타입으로 비워둠.
			HttpEntity<String> entity = new HttpEntity<>(headers);

			// 한투 서버 응답에는 Body와 HTTP 상태 코드, Header 존재함. output1, output2 중 output2만 꺼내서 사용함.
			ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

			// 4. output2 응답 데이터 처리
			List<Map<String, Object>> output2 = (List<Map<String, Object>>) response.getBody().get("output2");
			if (output2 != null && !output2.isEmpty()) {
				Map<String, Object> latestItem = output2.get(0);
				DayMinuteFlatDTO latestDto = parseToDTO(latestItem);
				redisChartService.insertMinuteData(stockCode, latestDto);
			}
		} catch (Exception e) {
			log.error("KIS API 호출 중 에러 발생 (종목코드: {}): {}", stockCode, e.getMessage());
		}
	}

	private DayMinuteFlatDTO parseToDTO(Map<String, Object> item) {
		return DayMinuteFlatDTO.builder().stckCntgHour(String.valueOf(item.get("stck_cntg_hour")))
				.stckOprc(Long.parseLong(item.get("stck_oprc").toString()))
				.stckHgpr(Long.parseLong(item.get("stck_hgpr").toString()))
				.stckLwpr(Long.parseLong(item.get("stck_lwpr").toString()))
				.stckPrpr(Long.parseLong(item.get("stck_prpr").toString())).build();
	}
}
