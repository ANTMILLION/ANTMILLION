package com.antmillion.mypage.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockHoldingsDTO {
    private String name;           // 종목명 (DB)
    private String code;           // 종목코드 (DB)
    private long shares;           // 보유수량 (DB)
    private long buyPrice;         // 총 매수금액 (DB)
    private long avgBuyPrice;      // 평균 단가 (DB)
    
    private long currentPrice;     // 현재가 (API 계산)
    private long totalValue;       // 평가금액 (계산: 현재가 * 수량)
    private long profit;           // 평가손익 (계산: 평가금액 - 매수금액)
    private double profitRate;     // 수익률 (계산)
}