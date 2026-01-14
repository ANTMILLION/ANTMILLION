package com.antmillion.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter
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
