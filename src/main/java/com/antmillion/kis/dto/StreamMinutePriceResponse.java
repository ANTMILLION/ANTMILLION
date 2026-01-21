package com.antmillion.kis.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StreamMinutePriceResponse {
    @JsonProperty("rt_cd")
    private String returnCode;
    @JsonProperty("msg_cd")
    private String messageCode;
    private String msg1;

    private List<StreamMinutePrice> output2;
}