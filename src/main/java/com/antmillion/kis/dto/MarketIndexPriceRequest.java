package com.antmillion.kis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MarketIndexPriceRequest {
    @JsonProperty("FID_PERIOD_DIV_CODE")
    private String periodCode; //기간 D 고정
    @JsonProperty("FID_COND_MRKT_DIV_CODE")
    private String marketCode; //시장구분코드 U 고정
    @JsonProperty("FID_INPUT_ISCD")
    private String indexCode; //지수구분코드 코스피 0001, 코스닥 1001
    @JsonProperty("FID_INPUT_DATE_1")
    private String endDate; //오늘 날짜 고정. 이날 기준 100건의 데이터 반환
}
