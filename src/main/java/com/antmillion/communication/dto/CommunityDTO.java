package com.antmillion.communication.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommunityDTO {
	private Long postId;
    private Long userId;
    private String stockCode;
    private String content;
    private LocalDateTime postedDate;
}
