package com.antmillion.kis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockVolumeRank {
    @JsonProperty("hts_kor_isnm")
    private String stockName; //종목명
    @JsonProperty("mksc_shrn_iscd")
    private String stockCode; //종목코드
    @JsonProperty("data_rank")
    private String rank; //순위
    @JsonProperty("stck_prpr")
    private String presentPrice; //주식 현재가
    @JsonProperty("acml_vol")
    private String acmVolume; //누적 거래량
}
