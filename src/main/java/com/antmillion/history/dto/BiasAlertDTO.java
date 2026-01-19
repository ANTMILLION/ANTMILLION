package com.antmillion.history.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 매매 편향 경고 정보를 담는 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BiasAlertDTO {
    private String stockCode;          
    private String stockName;           
    private Integer quantity;           
    private BigDecimal avgPrice;        
    private BigDecimal currentPrice;    
    private BigDecimal profitRate;      
    private LocalDateTime purchaseDate; 
    private Long holdingDays;           
    private String biasType;            
    private Boolean hasAlert;           
}
