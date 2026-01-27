package com.antmillion.mypage.service;

import java.util.List;
import java.util.Map;

import com.antmillion.mypage.dto.ExecutedOrdersDTO;


public interface MyPageService {
	//1. 주식잔고
    List<Map<String, Object>> getStockHoldings(Long accountId);
    
    //2. 실현손익
    List<Map<String, Object>> getRealizedProfit(Long accountId, String startDate, String endDate);
    
    //3. 체결내역
    List<ExecutedOrdersDTO> getExecutedOrders(Long accountId, String startDate, String endDate);
    
    //4. 계좌관리
	Map<String, Object> getAccountInfo(Long accountId);

}