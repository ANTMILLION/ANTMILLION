package com.antmillion.mypage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecutedOrdersDTO {
    // 첫 번째 줄 데이터
    private String stockName;          // 종목명
    private int orderQty;              // 주문수량
    private int executedQty;           // 체결수량
    private int unexecutedQty;         // 미체결수량
    private long unexecutedAmount;      // 미체결금액
    private String orderTime;          // 주문시간

    // 두 번째 줄 데이터
    private String type;               // 매매구분 (BUY/SELL)
    private long orderPrice;           // 주문단가
    private Double executedPrice;      // 체결단가 (평균체결가)
    private long orderAmount;          // 주문금액
    private String stockCode;          // 종목코드
    private String executedTime;       // 체결시간
}
