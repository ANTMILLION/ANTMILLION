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
public class SocialDTO {
	private Long kakaoId;
	private Long userId;
}
