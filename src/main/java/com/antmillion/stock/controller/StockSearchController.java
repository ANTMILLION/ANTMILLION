package com.antmillion.stock.controller;

import com.antmillion.stock.dto.StockDTO;
import com.antmillion.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock/search")
@RequiredArgsConstructor
public class StockSearchController {

    private final StockService stockService;

    /***
     * 종목 검색 자동완성 API
     * @param searchKeyword 검색 키워드
     * @return 검색 결과 (최대 5개)
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<List<StockDTO>> autoComplete(@RequestParam("searchKeyword") String searchKeyword) {
        List<StockDTO> results = stockService.searchStocks(searchKeyword);
        return ResponseEntity.ok(results);
    }

    /**
     * 종목명 검증 API
     * @param stockName 종목명
     * @return 존재 여부 및 종목 코드
     */
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateStock(@RequestParam("stockName") String stockName) {
        StockDTO stock = stockService.getStockByName(stockName);
        Map<String, Object> response = new HashMap<>();

        if (stock != null) {
            response.put("exists", true);
            response.put("stockCode", stock.getStockCode());
            response.put("stockName", stock.getStockName());
        } else {
            response.put("exists", false);
        }
        return ResponseEntity.ok(response);
    }

}
