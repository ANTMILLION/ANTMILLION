package com.antmillion.kis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StreamMinutePriceRequest {
	@JsonProperty("FID_COND_MRKT_DIV_CODE")
	private String condMrktDivCode; // 조건 시장 분류 코드 (J : KRX)
	@JsonProperty("FID_INPUT_ISCD")
	private String inputIscd;// 입력 종목코드
	@JsonProperty("FID_INPUT_HOUR_1")
	private String inputHour1;// 입력 시간1 (13시 130000)
	@JsonProperty("FID_INPUT_DATE_1")
	private String inputDate1;// 입력 날짜 (20260120)
	@JsonProperty("FID_PW_DATA_INCU_YN")
	private String pwDataIncuYn;// 과거 데이터 포함 여부 (당일 실시간 : N)
	@JsonProperty("FID_FAKE_TICK_INCU_YN")
	private String fakeTickIncuYn;// 허봉 포함 여부 (공백 필수 입력)
}
