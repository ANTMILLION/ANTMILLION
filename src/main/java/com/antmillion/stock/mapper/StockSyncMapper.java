package com.antmillion.stock.mapper;

import com.antmillion.stock.dto.StockDTO;
import org.springframework.data.repository.query.Param;

public interface StockSyncMapper {
    void truncateTemp();
    void insertTempStock(StockDTO stock);
    int insertNewStocks();
    int deleteOldStocks();
    int countStock();
}
