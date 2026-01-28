package com.antmillion.stock.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class StockOrderDTO {
    private Long orderId;
    private Long accountId;
    private String stockCode;
    private String transactionType; 
    private String orderType; 
    private Integer quantity;
    private Integer orderPrice;
    private String status; 
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String stockName;
}