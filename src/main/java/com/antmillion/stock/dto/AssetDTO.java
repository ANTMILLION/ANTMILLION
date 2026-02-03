package com.antmillion.stock.dto;

import java.math.BigDecimal;
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
public class AssetDTO {
	private Long assetId;
    private Long accountId;        // FK -> account.account_id
    private String stockCode;      // FK -> stock.stock_code
    private Integer quantity;
    private LocalDateTime updatedAt;
    private Long purchaseAmount;
    private double avgPrice;   // DECIMAL(15,2)
}
