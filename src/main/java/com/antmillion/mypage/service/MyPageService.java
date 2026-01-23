package com.antmillion.mypage.service;

import java.util.List;
import java.util.Map;

public interface MyPageService {
	//1. 주식잔고
    // 계좌 ID를 기반으로 보유 주식 목록(종목명 포함)을 가져옵니다.
    List<Map<String, Object>> getStockHoldings(Long accountId);
    
    //2. 실현손익
    List<Map<String, Object>> getRealizedProfit(Long accountId, String startDate, String endDate);
    
    //3. 체결내역
    

    //4. 계좌관리
	Map<String, Object> getAccountInfo(Long accountId);
}