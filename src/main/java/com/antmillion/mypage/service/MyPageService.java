package com.antmillion.mypage.service;

import java.util.List;
import java.util.Map;

import com.antmillion.mypage.dto.AccountInfoDTO;
import com.antmillion.mypage.dto.ExecutedOrdersDTO;
import com.antmillion.mypage.dto.RealizedProfitDTO;
import com.antmillion.mypage.dto.StockHoldingsDTO;


public interface MyPageService {
	//1. 주식잔고
	List<StockHoldingsDTO> getStockHoldings(Long accountId);
	
    //2. 실현손익
	List<RealizedProfitDTO> getRealizedProfit(Long accountId, String startDate, String endDate);
	
    //3. 체결내역
    List<ExecutedOrdersDTO> getExecutedOrders(Long accountId, String startDate, String endDate);
    
    //4. 계좌관리
    AccountInfoDTO getAccountInfo(Long accountId);
}