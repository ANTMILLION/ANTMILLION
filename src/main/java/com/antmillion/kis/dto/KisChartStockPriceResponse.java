package com.antmillion.kis.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KisChartStockPriceResponse {

    private String rt_cd;
    private String msg_cd;
    private String msg1;

    private List<ChartStockPrice> output2;

}
