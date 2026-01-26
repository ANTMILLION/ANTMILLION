package com.antmillion.kis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KisWebSocketRequest {
    @JsonProperty("grant_type")
    private String grantType = "client_credentials";
    private String appkey;
    private String secretkey; // appsecret과 동일
}
