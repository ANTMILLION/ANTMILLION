package com.antmillion.stock.service;

import com.antmillion.stock.dto.StockDTO;

import java.util.List;

public interface StockService {
    //검색어로 종목 검색
    List<StockDTO> searchStocks(String searchKeyword);

    //종목명으로 종목 조회
    StockDTO getStockByName(String stockName);

    //종목코드로 종목 조회
    StockDTO getStockByCode(String stockCode);
}
