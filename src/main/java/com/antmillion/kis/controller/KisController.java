package com.antmillion.kis.controller;

import com.antmillion.kis.dto.*;
import com.antmillion.kis.service.KisApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
     * 일봉 : 60일치
     * 주봉 : 52주치
     * 월봉 : 24개월치
     * 년봉 : 10년치
     */
    @GetMapping("/periodChart/{stockCode}")
    public List<ChartStockPrice> periodChart(
            @PathVariable("stockCode") String stockCode,
            @RequestParam("period") String period
    ) {
        ZoneId KST = ZoneId.of("Asia/Seoul");
        LocalDateTime endDate = LocalDateTime.now(KST);

        LocalDateTime startDate;
        switch (period) {
            case "W": { startDate = endDate.minusWeeks(52); break; }
            case "M": { startDate = endDate.minusMonths(48); break; }
            case "Y": { startDate = endDate.minusYears(30); break; }
            default: { startDate = endDate.minusDays(100); }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String startDateStr = startDate.format(formatter);
        String endDateStr = endDate.format(formatter);
        ChartStockPriceRequest request = ChartStockPriceRequest.builder()
                .marketCode("J") //KRX 고정
                .stockCode(stockCode)
                .periodCode(period)
                .startDate(startDateStr)
                .endDate(endDateStr)
                .adjPrice("0") //수정주가 고정
                .build();
        return kisApiService.getPeriodStockPrices(request);
    }

    @GetMapping("/marketIndex/{indexCode}")
    public List<MarketIndexPrice> marketIndexChart(
        @PathVariable("indexCode") String indexCode
    ) {
        ZoneId KST = ZoneId.of("Asia/Seoul");
        LocalDateTime endDate = LocalDateTime.now(KST);
        String endDateStr = endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        MarketIndexPriceRequest request = MarketIndexPriceRequest.builder()
                .periodCode("D")
                .marketCode("U")
                .indexCode(indexCode)
                .endDate(endDateStr)
                .build();
        return kisApiService.getMarketIndexPrices(request);
    }
    
    // 국내 일별 분봉 조회
    @GetMapping("/stream/{stockCode}")
    public List<StreamMinutePrice> getStreamMinute(@PathVariable String stockCode) {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String today = localDate.format(formatter);
    	
    	StreamMinutePriceRequest request = StreamMinutePriceRequest.builder()
                .condMrktDivCode("J")
                .inputIscd(stockCode)
                .inputHour1("153000")
                .inputDate1(today)
                .pwDataIncuYn("N")
                .fakeTickIncuYn("")
                .build();

        return kisApiService.getStreamMinutePrices(request);
    }
        // 국내 당일 분봉 조회
    @GetMapping("/candle/{stockCode}")
    public List<DayMinutePrice> getRealtimeCandle(@PathVariable String stockCode) {
        DayMinutePriceRequest request = DayMinutePriceRequest.builder()
                .condMrktDivCode("J")
                .inputIscd(stockCode)
                .pwDataIncuYn("Y") // 과거 데이터 30개 포함
                .etcClsCode("0")
                .build();
        return kisApiService.getDayMinutePrices(request);
    }
    // 거래량순 조회
    @GetMapping("/volumeRank")
    public List<StockVolumeRank> kisStockVolumeRank() {
        StockVolumeRankRequest request = StockVolumeRankRequest.builder()
                .marketCode("J") //고정 : KRX
                .screenCode("20171") //고정
                .inputCode("0000") //고정 : 종목코드 전체
                .divClassCode("0") //고정 : 전체 (보통주, 우선주)
                .blngClassCode("0") //고정 : 평균거래량
                .targetClassCode("111111111") //고정
                .targetExlsClassCode("0000001100") //고정 : ETF, ETN 제외
                .inputPrice1("0") //고정 : 전체 가격 대상
                .inputPrice2("0") //고정 : 전체 가격 대상
                .volumeCount("0") //고정 : 전체 거래량 대상
                .inputDate1("0") //고정
                .build();
        return kisApiService.getStockVolumeRanks(request);
    }

}
