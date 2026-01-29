package com.antmillion.kis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrentPrice {
    private String stockCode;
    private String stockName;
    @JsonProperty("stck_prpr")
    private String currentPrice;
    @JsonProperty("prdy_ctrt")
    private String prdyCtrt; //전일 대비율
}
