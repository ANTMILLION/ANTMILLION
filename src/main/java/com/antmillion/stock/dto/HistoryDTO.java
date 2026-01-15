package com.antmillion.stock.dto;

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
public class HistoryDTO {
	private Long history_id;
    private Long user_id;
    private Long message_id;
    private String stock_code;
    private String message_detail;
    private LocalDateTime time;
    private Boolean is_read;
}
