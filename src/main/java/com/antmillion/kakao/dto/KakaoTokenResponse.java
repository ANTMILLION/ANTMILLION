package com.antmillion.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KakaoTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("refresh_token_expires_in")
    private Long refreshTokenExpiresIn;

    private String scope;

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public Long getExpiresIn() { return expiresIn; }
    public Long getRefreshTokenExpiresIn() { return refreshTokenExpiresIn; }
    public String getScope() { return scope; }
}
