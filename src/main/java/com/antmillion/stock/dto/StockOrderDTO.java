package com.antmillion.stock.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class StockOrderDTO { // 주문 테이블과 1:1 매핑됨
    private Long orderId; // 주문 ID
    private Long accountId; // 계좌 ID
    private String stockCode; // 종목 코드
    private String transactionType; // 거래 타입(매수, 매도)
    private String orderType; // 주문 유형(지정가, 시장가)
    private Integer quantity; // 주문 수량
    private Integer orderPrice; // 주문 가격
    private String status; // 주문 상태(COMPLETED, WAIT, CANCEL, PARTIAL)
    private LocalDateTime createdAt; // 주문 생성 시간
    private LocalDateTime updatedAt; // 마지막 변경 시간
}