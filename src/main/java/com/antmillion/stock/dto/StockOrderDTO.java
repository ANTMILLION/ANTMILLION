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
public class StockOrderDTO {
	private Long orderId;
    private Long accountId;
    private String stockCode;
    private String transactionType;
    private String orderType;
    private Integer quantity;
    private Long orderPrice;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
