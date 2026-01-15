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
	private Long historyId;
    private Long userId;
    private Long messageId;
    private String stockCode;
    private String messageDetail;
    private LocalDateTime time;
    private Boolean isRead;
}
