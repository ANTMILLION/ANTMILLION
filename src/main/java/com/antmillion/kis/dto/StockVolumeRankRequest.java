package com.antmillion.kis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StockVolumeRankRequest {
    @JsonProperty("FID_COND_MRKT_DIV_CODE")
    private String marketCode;
    @JsonProperty("FID_COND_SCR_DIV_CODE")
    private String screenCode;
    @JsonProperty("FID_INPUT_ISCD")
    private String inputCode;
    @JsonProperty("FID_DIV_CLS_CODE")
    private String divClassCode;
    @JsonProperty("FID_BLNG_CLS_CODE")
    private String blngClassCode;
    @JsonProperty("FID_TRGT_CLS_CODE")
    private String targetClassCode;
    @JsonProperty("FID_TRGT_EXLS_CLS_CODE")
    private String targetExlsClassCode;
    @JsonProperty("FID_INPUT_PRICE_1")
    private String inputPrice1;
    @JsonProperty("FID_INPUT_PRICE_2")
    private String inputPrice2;
    @JsonProperty("FID_VOL_CNT")
    private String volumeCount;
    @JsonProperty("FID_INPUT_DATE_1")
    private String inputDate1;
}
