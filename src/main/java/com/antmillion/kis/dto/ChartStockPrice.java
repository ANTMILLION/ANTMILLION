package com.antmillion.kis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChartStockPrice {
    @JsonProperty("stck_bsop_date")
    private String stockDate; //영업 일자
    @JsonProperty("stck_clpr")
    private String closePrice; //주식 종가
    @JsonProperty("stck_oprc")
    private String openPrice; //주식 시가
    @JsonProperty("stck_hgpr")
    private String highPrice; //주식 최고가
    @JsonProperty("stck_lwpr")
    private String lowPrice; //주식 최저가
}
