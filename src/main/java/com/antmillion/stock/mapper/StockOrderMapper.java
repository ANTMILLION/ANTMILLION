package com.antmillion.stock.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.antmillion.stock.dto.StockOrderDTO;
import com.antmillion.stock.dto.StockOrderResponseDTO;

@Mapper
public interface StockOrderMapper {

    /**
     * 주문 생성
     */
    int insertOrder(StockOrderDTO order);

    /**
     * 특정 계좌의 미체결(WAIT, PARTIAL) 화면 조회
     */
    List<StockOrderResponseDTO> selectWaitOrders(@Param("accountId") Long accountId);

    /**
     * 주문 취소
     */
    int cancelOrder(@Param("orderId") Long orderId, @Param("accountId") Long accountId);

    /**
     * 주문 정정
     */
    int updateOrder(StockOrderDTO order);

    /**
     * 주문 아이디로 단일 주문 정보 조회
     */
    StockOrderResponseDTO getOrderByOrderId(@Param("orderId") Long orderId);

    /**
     * 체결 조건이 충족된 주문 목록 조회
     */
    List<StockOrderResponseDTO> findExecutableOrders(@Param("stockCode") String stockCode, @Param("currentPrice") int currentPrice);

    /**
     * 주문 상태 업데이트
     */
    void updateOrderStatus(@Param("orderId") Long orderId, @Param("status") String status);
    
    /**
     * 미체결 주문 일괄 취소
     */
    int cancelRemainingOrders();
    
    /**
     * 주문수량-체결수량 합계 (미체결 수량 합계)
     */
    Integer getSumUnexecutedQty(@Param("accountId") Long accountId, @Param("stockCode") String stockCode, @Param("type") String type);
}
