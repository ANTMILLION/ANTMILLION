package com.antmillion.myinfo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MyAuthInfoDTO {
    private Long userId;
    private String provider;
    private String password; // bcrypt 해시
}
