package com.antmillion.stock.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
    @Select("SELECT o.*, s.stock_name " +
            "FROM stock_order o " +
            "JOIN stock s ON o.stock_code = s.stock_code " +
            "WHERE o.account_id = #{accountId} AND o.status = 'WAIT' " +
            "ORDER BY o.created_at DESC")
    List<StockOrderDTO> selectWaitOrders(@Param("accountId") Long accountId);
    
    /**
     * 주문 취소
     */
    @Update("UPDATE stock_order SET status = 'CANCEL', updated_at = NOW() " +
            "WHERE order_id = #{orderId} AND status = 'WAIT' AND account_id = #{accountId}")
    int cancelOrder(@Param("orderId") Long orderId, @Param("accountId") Long accountId);
    
    /**
     * 주문 정정
     */
    @Update("UPDATE stock_order " +
            "SET order_price = #{orderPrice}, " +
            "    quantity = #{quantity}, " +
            "    updated_at = NOW() " +
            "WHERE order_id = #{orderId} " +
            "  AND status = 'WAIT' " +
            "  AND account_id = #{accountId} ")
    int updateOrder(StockOrderDTO order);
    
    /**
     * 주문 아이디로 단일 주문 정보 조회 (정정/취소 검증용)
     */
    @Select("SELECT * FROM stock_order WHERE order_id = #{orderId}")
    StockOrderDTO getOrderByOrderId(@Param("orderId") Long orderId);
}
