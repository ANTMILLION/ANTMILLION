package com.antmillion.kis.controller;

import com.antmillion.kis.dto.ChartStockPrice;
import com.antmillion.kis.dto.ChartStockPriceRequest;
import com.antmillion.kis.service.KisApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/kis")
public class KisController {

    private final KisApiService kisApiService;

    /**
     * 한국투자증권 api의 응답을 확인하기위한 컨트롤러
     * 실제로 사용할 때는 KisService만 사욯하면 될듯
     */

    @GetMapping("/auth")
    public String auth() {
        return kisApiService.getKisAccessToken();
    }

    /**
     * 삼성전자 26년 1월 1일부터 26년 1월 17일까지 일봉 데이터 반환 테스트
     */
    @GetMapping("/periodChart")
    public List<ChartStockPrice> periodChart(
    ) {
        ChartStockPriceRequest request = ChartStockPriceRequest.builder()
                .marketCode("J") //KRX 고정
                .stockCode("005930")
                .periodCode("D")
                .startDate("20260101")
                .endDate("20260117")
                .adjPrice("0") //수정주가 고정
                .build();
        return kisApiService.getPeriodStockPrices(request);
    }


}
