package com.antmillion.kis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StreamMinutePrice {
	@JsonProperty("stck_bsop_date")
	private String stckBsopDate; // 주식 영업 일자
	@JsonProperty("stck_cntg_hour")
	private String stckCntgHour; // 주식 체결 시간
	@JsonProperty("stck_prpr")
	private String stckPrpr; // 주식 현재가(종가)
	@JsonProperty("stck_oprc")
	private String stckOprc; // 주식 시가2
	@JsonProperty("stck_hgpr")
	private String stckHgpr; // 주식 최고가
	@JsonProperty("stck_lwpr")
	private String stckLwpr; // 주식 최저가
}
