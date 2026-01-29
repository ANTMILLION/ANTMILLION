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
	@Insert("INSERT INTO stock_order (account_id, stock_code, transaction_type, order_type, quantity, order_price, status, created_at, updated_at) "
			+ "VALUES (#{accountId}, #{stockCode}, #{transactionType}, #{orderType}, #{quantity}, #{orderPrice}, #{status}, #{createdAt}, #{updatedAt})")
	@Options(useGeneratedKeys = true, keyProperty = "orderId")
	int insertOrder(StockOrderDTO order);

	/**
	 * 특정 계좌의 미체결(WAIT, PARTIAL) 화면 조회용 서브쿼리 대신 JOIN을 사용하여 성능 최적화
	 */
	@Select("SELECT o.*, s.stock_name, " + "COALESCE(SUM(t.trade_quantity), 0) as executedQuantity, "
			+ "(o.quantity - COALESCE(SUM(t.trade_quantity), 0)) as remainedQuantity " + "FROM stock_order o "
			+ "JOIN stock s ON o.stock_code = s.stock_code " + "LEFT JOIN trade_log t ON o.order_id = t.order_id "
			+ "WHERE o.account_id = #{accountId} AND o.status IN ('WAIT', 'PARTIAL') " + "GROUP BY o.order_id "
			+ "ORDER BY o.created_at DESC")
	List<StockOrderResponseDTO> selectWaitOrders(@Param("accountId") Long accountId);

	/**
	 * 주문 취소: WAIT(대기) 또는 PARTIAL(부분체결) 상태일 때만 취소 가능
	 */
	@Update("UPDATE stock_order SET status = 'CANCEL', updated_at = NOW() " + "WHERE order_id = #{orderId} "
			+ "AND account_id = #{accountId} " + "AND status IN ('WAIT', 'PARTIAL')")
	int cancelOrder(@Param("orderId") Long orderId, @Param("accountId") Long accountId);

	/**
	 * 주문 정정
	 */
	@Update("UPDATE stock_order " + "SET order_price = #{orderPrice}, " + "    quantity = #{quantity}, "
			+ "    updated_at = NOW() " + "WHERE order_id = #{orderId} " + "  AND status = 'WAIT' "
			+ "  AND account_id = #{accountId} ")
	int updateOrder(StockOrderDTO order);

	/**
	 * 주문 아이디로 단일 주문 정보 조회 (정정/취소 검증용)
	 */
	@Select("SELECT * FROM stock_order WHERE order_id = #{orderId}")
	StockOrderResponseDTO getOrderByOrderId(@Param("orderId") Long orderId);

	// 특정 종목의 현재가에 도달하여 체결 조건이 충족된 대기(WAIT) 또는 부분 체결(PARTIAL) 상태의 주문 목록 조회
	@Select("SELECT o.*, s.stock_name, COALESCE(SUM(t.trade_quantity), 0) as executedQuantity " +
            "FROM stock_order o " +
            "JOIN stock s ON o.stock_code = s.stock_code " +
            "LEFT JOIN trade_log t ON o.order_id = t.order_id " +
            "WHERE o.stock_code = #{stockCode} AND o.status IN ('WAIT', 'PARTIAL') " +
            "AND ((o.transaction_type = 'BUY' AND o.order_price >= #{currentPrice}) " +
            "     OR (o.transaction_type = 'SELL' AND o.order_price <= #{currentPrice})) " +
            "GROUP BY o.order_id ORDER BY o.created_at ASC")
    List<StockOrderResponseDTO> findExecutableOrders(@Param("stockCode") String stockCode, @Param("currentPrice") int currentPrice);

	// 주문 상태 업데이트
	@Update("UPDATE stock_order SET status = #{status}, updated_at = NOW() WHERE order_id = #{orderId}")
	void updateOrderStatus(@Param("orderId") Long orderId, @Param("status") String status);
}
