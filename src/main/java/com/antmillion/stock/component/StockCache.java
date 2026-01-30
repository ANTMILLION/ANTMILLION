package com.antmillion.stock.component;

import com.antmillion.stock.dto.StockDTO;
import com.antmillion.stock.mapper.StockMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class StockCache {

    private final StockMapper stockMapper;
    private final Map<String, StockDTO> cache = new ConcurrentHashMap<>();

    public StockCache(StockMapper stockMapper) {
        this.stockMapper = stockMapper;
    }

    //cache에 미리 DB에서 모든 데이터 로드해두기
    @PostConstruct
    public void init(){
        stockMapper.findAll().forEach(s -> {
               cache.put(
                    s.getStockCode(),
                    new StockDTO(s.getStockCode(), s.getStockName(), null));});
    }

    //서버에서 필터링하여 결과 전달
    public List<StockDTO> searchStocks(String searchKeyword) {
        return cache.values().stream()
                .filter(s -> s.getStockName().toLowerCase().contains(searchKeyword.toLowerCase()))
                .sorted(Comparator.comparing(StockDTO::getStockName))
                .collect(Collectors.toList());
    }

    //서버에서 확인하여 결과 전달
    public StockDTO getStockByName(String stockName) {
        return cache.values().stream()
                .filter(s -> s.getStockName().equals(stockName))
                .findFirst()
                .orElse(null);
    }

    public StockDTO getStockByCode(String stockCode) {
        return cache.values().stream()
                .filter(s -> s.getStockCode().equals(stockCode))
                .findFirst()
                .orElse(null);
    }

    // 캐시 갱신 메서드 추가
    public void refresh() {
        cache.clear();
        stockMapper.findAll().forEach(s -> {
            cache.put(
                    s.getStockCode(),
                    new StockDTO(s.getStockCode(), s.getStockName(), null));
        });
    }
}
