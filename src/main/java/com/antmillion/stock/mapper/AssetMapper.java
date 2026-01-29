package com.antmillion.stock.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AssetMapper {

	/**
	 * 특정 계좌의 특정 종목 보유 수량 조회 (합산)
	 */
	@Select("SELECT COALESCE(SUM(quantity), 0) " + "FROM asset "
			+ "WHERE account_id = #{accountId} AND stock_code = #{stockCode}")
	Integer getQuantityByAccountAndStock(@Param("accountId") Long accountId, @Param("stockCode") String stockCode);

	/**
	 * [매수 정합성] UPSERT: 수량 증가, 단가 갱신
	 */
	@Insert("INSERT INTO asset (account_id, stock_code, quantity, purchase_amount, avg_price, updated_at) "
			+ "VALUES (#{accountId}, #{stockCode}, #{quantity}, #{purchaseAmount}, #{avgPrice}, NOW()) "
			+ "ON DUPLICATE KEY UPDATE "
			+ "   avg_price = (purchase_amount + VALUES(purchase_amount)) / (quantity + VALUES(quantity)), "
			+ "   quantity = quantity + VALUES(quantity), "
			+ "   purchase_amount = purchase_amount + VALUES(purchase_amount), " + "   updated_at = NOW()")
	int upsertAssetBuy(@Param("accountId") Long accountId, @Param("stockCode") String stockCode,
			@Param("quantity") Integer quantity, @Param("avgPrice") Integer avgPrice,
			@Param("purchaseAmount") Long purchaseAmount);

	/**
	 * [매도 정합성] UPDATE: 수량 감소, 총액 감소 (단가는 유지)
	 */
	@Update("UPDATE asset SET " + "   quantity = quantity - #{quantity}, "
			+ "   purchase_amount = purchase_amount - (avg_price * #{quantity}), " + "   updated_at = NOW() "
			+ "WHERE account_id = #{accountId} AND stock_code = #{stockCode} AND quantity >= #{quantity}")
	int updateAssetSell(@Param("accountId") Long accountId, @Param("stockCode") String stockCode,
			@Param("quantity") Integer quantity);

	/**
	 * 매도 체결 시 돈 입금
	 */
	@Update("UPDATE account SET balance = balance + #{amount} WHERE account_id = #{accountId}")
	int addBalance(@Param("accountId") Long accountId, @Param("amount") long amount);
	
	/**
	 * 매수 체결 시 돈 차감
	 */
	@Update("UPDATE account SET balance = balance - #{amount} WHERE account_id = #{accountId} AND balance >= #{amount}")
	int minusBalance(@Param("accountId") Long accountId, @Param("amount") long amount);
	
	/**
	 * 수량이 0이 된 주식 데이터 제거
	 */
	@Delete("DELETE FROM asset WHERE account_id = #{accountId} AND stock_code = #{stockCode} AND quantity <= 0")
	int deleteZeroQuantityAsset(@Param("accountId") Long accountId, @Param("stockCode") String stockCode);
}
