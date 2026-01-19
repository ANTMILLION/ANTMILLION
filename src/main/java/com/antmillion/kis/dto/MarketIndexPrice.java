package com.antmillion.kis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarketIndexPrice {
    @JsonProperty("stck_bsop_date")
    private String stockDate; //영업 일자
    @JsonProperty("bstp_nmix_prpr")
    private String presentPrice; //지수 현재가 (종가)
    @JsonProperty("bstp_nmix_oprc")
    private String openPrice; //지수 시가
    @JsonProperty("bstp_nmix_hgpr")
    private String highPrice; //지수 최고가
    @JsonProperty("bstp_nmix_lwpr")
    private String lowPrice; //지수 최저가
}
