package com.antmillion.mypage.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.antmillion.mypage.dto.ExecutedOrdersDTO;

@Mapper
public interface MyPageMapper {
	// 특정 계좌의 주식 잔고 목록 조회
    List<Map<String, Object>> selectStockHoldings(Long accountId);
    
    // 실현손익 목록 조회 (명칭 통일 및 파라미터 Map으로 변경)
    List<Map<String, Object>> selectRealizedProfit(Map<String, Object> params);
    
 // MyPageMapper.java
    List<ExecutedOrdersDTO> selectExecutedOrders(
        @Param("accountId") Long accountId, 
        @Param("startDate") String startDate, 
        @Param("endDate") String endDate
    );
    
    // 계좌정보 조회
    Map<String, Object> selectAccountInfo(Long accountId);

}