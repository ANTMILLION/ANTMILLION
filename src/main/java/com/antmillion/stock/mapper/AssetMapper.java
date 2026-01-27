package com.antmillion.stock.mapper;

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
    @Select("SELECT COALESCE(SUM(quantity), 0) " +
            "FROM asset " +
            "WHERE account_id = #{accountId} AND stock_code = #{stockCode}")
    Integer getQuantityByAccountAndStock(@Param("accountId") Long accountId, 
                                         @Param("stockCode") String stockCode);
    
    /**
     * 자산 추가 (신규 매수)
     */
    @Insert("INSERT INTO asset (account_id, stock_code, quantity, avg_price, purchase_amount, updated_at) " +
            "VALUES (#{accountId}, #{stockCode}, #{quantity}, #{avgPrice}, #{purchaseAmount}, NOW())")
    int insertAsset(@Param("accountId") Long accountId,
                    @Param("stockCode") String stockCode,
                    @Param("quantity") Integer quantity,
                    @Param("avgPrice") Integer avgPrice,
                    @Param("purchaseAmount") Long purchaseAmount);
    
    /**
     * 자산 수량 감소 (매도)
     * 가장 오래된 자산부터 차감
     */
    @Update("UPDATE asset " +
            "SET quantity = quantity - #{quantity}, " +
            "    updated_at = NOW() " +
            "WHERE account_id = #{accountId} " +
            "AND stock_code = #{stockCode} " +
            "AND quantity > 0 " +
            "ORDER BY updated_at ASC " +
            "LIMIT 1")
    int decreaseAssetQuantity(@Param("accountId") Long accountId,
                              @Param("stockCode") String stockCode,
                              @Param("quantity") Integer quantity);
}