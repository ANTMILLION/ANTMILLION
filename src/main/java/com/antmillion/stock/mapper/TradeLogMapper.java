package com.antmillion.stock.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TradeLogMapper {

    /**
     * trade_log 테이블에 새로운 체결 행 삽입
     */
    int insertTradeLog(@Param("orderId") Long orderId, 
                       @Param("tradePrice") long tradePrice,
                       @Param("tradeQuantity") int tradeQuantity);

    /**
     * 특정 주문번호(order_id)에 대해 지금까지 체결된 총 수량 조회
     */
    int getTotalExecutedQty(@Param("orderId") Long orderId);

}
