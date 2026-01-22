package com.antmillion.stock.service;

import com.antmillion.stock.component.StockCache;
import com.antmillion.stock.dto.StockDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockCache stockCache;

    //검색어로 시작하는 종목 최대 5개까지 조회
    @Override
    public List<StockDTO> searchStocks(String searchKeyword) {
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            return List.of();
        }
        return stockCache.searchStocks(searchKeyword.trim());
    }

    //종목 이름으로 조회
    @Override
    public StockDTO getStockByName(String stockName) {
        if (stockName == null || stockName.trim().isEmpty()) {
            return null;
        }
        return stockCache.getStockByName(stockName.trim());
    }

    @Override
    public StockDTO getStockByCode(String stockCode) {
        return stockCache.getStockByCode(stockCode);
    }

}
