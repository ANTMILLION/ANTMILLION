package com.antmillion.myinfo.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyInfoView {
    private final String nickname;
    private final String emailDisplay;
    private final boolean isKakao;
    private final LocalDateTime joinDate;
}
