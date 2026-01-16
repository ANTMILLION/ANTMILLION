package com.antmillion.kis.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KisAccessTokenRequest {
    private String grant_type = "client_credentials";
    private String appkey;
    private String appsecret;
}
