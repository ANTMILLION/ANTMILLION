package com.antmillion.mypage.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealizedProfitDTO {
    private String name;           // 종목명
    private long shares;           // 매도수량
    private long buyPrice;         // 매수단가 (평균단가)
    private long sellPrice;        // 매도단가
    private long profit;           // 실현손익
    private double profitRate;     // 수익률 (실현손익 / 차감원가)
    private String tradeDate;      // 거래일시 (날짜 포맷팅)
}