package com.antmillion.mypage.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyPageMapper {
    // 특정 계좌의 주식 잔고 목록 조회 (종목 정보 포함)
    List<Map<String, Object>> selectStockHoldings(Long accountId);
    
    List<Map<String, Object>> selectRealizedProfit(Long accountId);
}