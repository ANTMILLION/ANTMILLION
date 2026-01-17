package com.antmillion.kis.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChartStockPriceRequest {
    @JsonProperty("FID_COND_MRKT_DIV_CODE")
    private String marketCode;
    @JsonProperty("FID_INPUT_ISCD")
    private String stockCode;
    @JsonProperty("FID_INPUT_DATE_1")
    private String startDate;
    @JsonProperty("FID_INPUT_DATE_2")
    private String endDate;
    @JsonProperty("FID_PERIOD_DIV_CODE")
    private String periodCode;
    @JsonProperty("FID_ORG_ADJ_PRC")
    private String adjPrice;
}
