package com.antmillion.myinfo.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MyInfoDTO {
    private Long userId;
    private String nickname;
    private String email;
    private String provider;
    private LocalDateTime joinDate;
}
