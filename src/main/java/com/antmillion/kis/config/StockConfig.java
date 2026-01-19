// 주식당일분봉조회-차트당일분봉
package com.antmillion.kis.config;

import java.util.List;

// 스케줄러가 가져올 종목 제한
public class StockConfig {
    public static final List<String> TARGET_STOCKS = List.of(
        "005930", // 삼성전자
        "000660", // SK하이닉스
        "373220", // LG에너지솔루션
        "207940", // 삼성바이오로직스
        "005380", // 현대차
        "035420", // NAVER
        "035720", // 카카오
        "000270", // 기아
        "068270", // 셀트리온
        "005490", // POSCO홀딩스
        "105560", // KB금융
        "055550", // 신한지주
        "051910", // LG화학
        "032830", // 삼성생명
        "000810", // 삼성화재
        "012330", // 현대모비스
        "066570"  // LG전자
    );
}
