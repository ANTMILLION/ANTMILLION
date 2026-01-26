package com.antmillion.kis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class KisWebSocketTransactionPriceResponse {
	@JsonProperty("MKSC_SHRN_ISCD")
    private String mkscShrnIscd; // 유가증권 단축 종목코드
	@JsonProperty("STCK_PRPR")
    private Integer stckPrpr; // 주식 현재가
	@JsonProperty("PRDY_VRSS")
	private Integer prdyVrss; // 전일 대비
	@JsonProperty("PRDY_CTRT")
    private Integer prdyCtrt; // 전일 대비율
	@JsonProperty("SHNU_RATE")
    private Integer shnuRate; // 매수비율
}
