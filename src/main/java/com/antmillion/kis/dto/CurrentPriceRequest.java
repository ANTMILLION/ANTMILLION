package com.antmillion.kis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CurrentPriceRequest {
    @JsonProperty("FID_COND_MRKT_DIV_CODE")
    private String marketCode;
    @JsonProperty("FID_INPUT_ISCD")
    private String stockCode;
}
