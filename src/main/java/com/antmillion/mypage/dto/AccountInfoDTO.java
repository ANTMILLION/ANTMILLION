package com.antmillion.mypage.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoDTO {
    private String nickname;    	// 닉네임
    private String accountNumber;   // 계좌번호
    private long balance;          	// 잔고
    private String createdAt;       // 개설일
}