package com.antmillion.stock.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StockOrderDTO {
	private Long order_id;
    private Long account_id;
    private String stock_code;
    private String transaction_type;
    private String order_type;
    private Integer quantity;
    private Long order_price;
    private String status;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
