package com.antmillion.kis.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class KisAccessToken {

    private Long id;
    private String token;
    private LocalDateTime expiresAt;  // 만료 시각 저장

}
