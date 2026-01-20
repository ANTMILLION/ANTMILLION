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
    private Long messageId;
    private Long orderId;
    private String stockCode;
    private String stockName;      // stock 테이블 조인
    private String messageType;    // message 테이블 조인 (SUNK_COST 등)
    private String messageContent; // message 테이블 조인 (제목)
    private String messageDetail;  // 상세 경고 내용
    private Date time;
    private boolean isRead;
    
    
    private String transactionType; // 매수/매도
    private int quantity;           // 수량
    private long orderPrice;        // 단가
    private long totalAmount;       // 총 금액
}