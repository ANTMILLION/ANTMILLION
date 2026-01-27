package com.antmillion.mypage.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.antmillion.mypage.dto.AccountInfoDTO;
import com.antmillion.mypage.dto.ExecutedOrdersDTO;
import com.antmillion.mypage.dto.RealizedProfitDTO;
import com.antmillion.mypage.dto.StockHoldingsDTO;

@Mapper
public interface MyPageMapper {
	// 1. 주식잔고 조회
    List<StockHoldingsDTO> selectStockHoldings(Long accountId);
    
    // 2. 실현손익 조회
    List<RealizedProfitDTO> selectRealizedProfit(Map<String, Object> params);
    
    // 3. 체결내역 조회
    List<ExecutedOrdersDTO> selectExecutedOrders(
        @Param("accountId") Long accountId, 
        @Param("startDate") String startDate, 
        @Param("endDate") String endDate
    );
    
    // 4. 계좌정보 조회
    AccountInfoDTO selectAccountInfo(Long accountId);
}