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
public class TradeLogDTO {
	private Long trade_id;
    private Long order_id;
    private LocalDateTime trade_date;
    private Long trade_price;
    private Integer trade_quantity;
}
