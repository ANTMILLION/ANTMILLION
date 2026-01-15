package com.antmillion.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberDTO {
	private Long userId;
    private Integer rankId;
    private String email;
    private String password;
    private String nickname;
    private Integer point;
    private String provider;
}
