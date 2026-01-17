package com.antmillion.kis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KisAccessTokenRequest {
    @JsonProperty("grant_type")
    private String grantType = "client_credentials";
    private String appkey;
    private String appsecret;
}
