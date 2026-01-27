package com.antmillion.stock.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

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
    
    
    /**
     * 특정 계좌의 대기(WAIT) 주문 목록 조회
     */
    @Select("SELECT * FROM stock_order " +
            "WHERE account_id = #{accountId} AND status = 'WAIT' " +
            "ORDER BY created_at DESC")
    List<StockOrderDTO> selectWaitOrders(@Param("accountId") Long accountId);
}