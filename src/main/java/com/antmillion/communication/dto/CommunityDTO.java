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
	private Long post_id;
    private Long user_id;
    private String stock_code;
    private String content;
    private LocalDateTime posted_date;
}
