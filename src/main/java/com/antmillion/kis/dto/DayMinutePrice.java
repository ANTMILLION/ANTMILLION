// 주식당일분봉조회-차트당일분봉
package com.antmillion.kis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DayMinutePrice {
	@JsonProperty("stck_cntg_hour")
	private String stckCntgHour; // 주식 체결시간
	@JsonProperty("stck_oprc")
	private long stckOprc; // 주식 시가
	@JsonProperty("stck_hgpr")
	private long stckHgpr; // 주식 최고가
	@JsonProperty("stck_lwpr")
	private long stckLwpr; // 주식 최저가
	@JsonProperty("stck_prpr")
	private long stckPrpr; // 주식 현재가(종가)
}
