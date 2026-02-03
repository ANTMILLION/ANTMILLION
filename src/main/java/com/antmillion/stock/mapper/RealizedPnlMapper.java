package com.antmillion.stock.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RealizedPnlMapper {
    //실현손익 데이터 저장
    int insertRealizedPnl(
            @Param("stockCode") String stockCode,
            @Param("tradeId") Long tradeId,
            @Param("accountId") Long accountId,
            @Param("sellDate") String sellDate,
            @Param("sellQuantity") int sellQuantity,
            @Param("sellAmount") long sellAmount,
            @Param("costBasis") long costBasis,
            @Param("realizedProfit") long realizedProfit,
            @Param("avgPrice") double avgPrice
    );
}
