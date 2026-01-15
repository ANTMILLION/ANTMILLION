package com.antmillion.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StockDTO {
	private String stock_code;
	private String stock_name;
	private String stock_image;
}
