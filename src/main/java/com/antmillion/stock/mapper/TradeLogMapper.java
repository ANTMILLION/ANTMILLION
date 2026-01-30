package com.antmillion.stock.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TradeLogMapper {

	/**
	 * trade_log 테이블에 새로운 체결 행 삽입 (주문 ID, 체결 시간, 체결가, 체결 수량)
	 */
	@Insert("INSERT INTO trade_log (order_id, trade_date, trade_price, trade_quantity) "
			+ "VALUES (#{orderId}, NOW(), #{tradePrice}, #{tradeQuantity})")
	int insertTradeLog(@Param("orderId") Long orderId, @Param("tradePrice") long tradePrice,
			@Param("tradeQuantity") int tradeQuantity);

	/**
	 * 특정 주문번호(order_id)에 대해 지금까지 체결된 총 수량 조회
	 */
	@Select("SELECT COALESCE(SUM(trade_quantity), 0) " + "FROM trade_log " + "WHERE order_id = #{orderId}")
	int getTotalExecutedQty(@Param("orderId") Long orderId);

}
