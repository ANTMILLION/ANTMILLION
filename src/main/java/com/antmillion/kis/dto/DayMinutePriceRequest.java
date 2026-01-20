package com.antmillion.kis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DayMinutePriceRequest {
	 @JsonProperty("FID_COND_MRKT_DIV_CODE")
	 private String condMrktDivCode; // 조건 시장 분류 코드(J : KRX)
	 @JsonProperty("FID_INPUT_ISCD")
	 private String inputIscd;// 입력 종목코드
	 @JsonProperty("FID_PW_DATA_INCU_YN")
	 private String pwDataIncuYn;// 과거 데이터 포함 여부(Y : 최근 30개 output2 받을 수 있음)
	 @JsonProperty("FID_ETC_CLS_CODE")
	 private String etcClsCode;// 기타 구분 코드("0")
}
