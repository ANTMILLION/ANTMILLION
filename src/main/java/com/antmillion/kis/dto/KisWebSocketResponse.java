package com.antmillion.kis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KisWebSocketResponse {
    @JsonProperty("approval_key")
    private String approvalKey; // 	웹소켓 접속키
}
