package com.antmillion.kis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KisStockVolumeRankResponse {
    @JsonProperty("rt_cd")
    private String returnCode;
    @JsonProperty("msg_cd")
    private String messageCode;
    private String msg1;

    private List<StockVolumeRank> output;
}
