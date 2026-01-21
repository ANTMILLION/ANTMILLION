package com.antmillion.stock.mapper;

import com.antmillion.stock.dto.StockDTO;

import java.util.List;

public interface StockMapper {
    //전 종목 조회
    List<StockDTO> findAll();
}
