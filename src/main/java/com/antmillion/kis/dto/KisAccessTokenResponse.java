package com.antmillion.kis.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KisAccessTokenResponse {
    private String access_token;
    private String token_type;
    private Long expires_in;
}
