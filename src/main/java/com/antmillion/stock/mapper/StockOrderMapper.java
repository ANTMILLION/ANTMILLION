package com.antmillion.stock.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import com.antmillion.stock.dto.StockOrderDTO;

@Mapper
public interface StockOrderMapper {

    /**
     * 주문 생성
     */
    @Insert("INSERT INTO stock_order (account_id, stock_code, transaction_type, order_type, quantity, order_price, status, created_at, updated_at) " +
            "VALUES (#{accountId}, #{stockCode}, #{transactionType}, #{orderType}, #{quantity}, #{orderPrice}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "orderId")
    int insertOrder(StockOrderDTO order);
}