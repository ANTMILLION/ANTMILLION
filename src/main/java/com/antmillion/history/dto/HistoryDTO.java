package com.antmillion.history.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HistoryDTO {
    private Long historyId;
    private Long userId;
    private Long orderId;
    private String stockCode;        
    private String stockName;        
    private String biasType;         
    private String messageDetail;    
    private Date time;
    private boolean isRead;
    
    private String transactionType;  
    private int quantity;           
    private long orderPrice;         
    private long totalAmount;        
}