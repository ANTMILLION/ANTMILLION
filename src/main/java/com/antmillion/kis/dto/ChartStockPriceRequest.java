package com.antmillion.kis.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChartStockPriceRequest {
    private String FID_COND_MRKT_DIV_CODE;
    private String FID_INPUT_ISCD;
    private String FID_INPUT_DATE_1;
    private String FID_INPUT_DATE_2;
    private String FID_PERIOD_DIV_CODE;
    private String FID_ORG_ADJ_PRC;
}
