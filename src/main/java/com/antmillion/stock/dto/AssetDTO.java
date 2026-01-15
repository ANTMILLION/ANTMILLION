package com.antmillion.stock.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AssetDTO {
	private Long asset_id;
    private Long account_id;        // FK -> account.account_id
    private String stock_code;      // FK -> stock.stock_code
    private Integer quantity;
    private LocalDateTime updated_at;
    private Long purchase_amount;
    private BigDecimal avg_price;   // DECIMAL(15,2)
}
