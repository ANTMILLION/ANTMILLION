package com.antmillion.kis.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChartStockPriceResponse {
    String stck_bsop_date; //영업 일자
    String stck_clpr; //주식 종가
    String stck_oprc; //주식 시가
    String stck_hgpr; //주식 최고가
    String stck_lwpr; //주식 최저가
}
