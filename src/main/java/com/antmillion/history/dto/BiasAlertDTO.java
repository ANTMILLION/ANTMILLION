package com.antmillion.history.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
@NoArgsConstructor    
@AllArgsConstructor
@Getter
@Builder
@ToString
public class BiasAlertDTO {
    

    private String stockCode;
    private String stockName;  
    private Integer quantity;
    private BigDecimal avgPrice;
    private BigDecimal currentPrice;
    private BigDecimal profitRate;  
    private LocalDateTime purchaseDate;
    private Integer holdingDays;
    private BiasType biasType;  
    private Boolean hasAlert;
    

    public String getBiasTypeCode() {
        return biasType != null ? biasType.getCode() : null;
    }
    

    public String getBiasTypeTitle() {
        return biasType != null ? biasType.getTitle() : null;
    }
    

    public String getBiasTypeMessage() {
        return biasType != null ? biasType.getMessage() : null;
    }
}