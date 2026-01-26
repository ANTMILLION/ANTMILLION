package com.antmillion.kis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrentPrice {
    @JsonProperty("stck_prpr")
    private String currentPrice;
}
