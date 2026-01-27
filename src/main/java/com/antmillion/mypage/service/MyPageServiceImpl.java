package com.antmillion.mypage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.antmillion.kis.dto.CurrentPriceRequest;
import com.antmillion.kis.service.KisApiService;
import org.springframework.stereotype.Service;

import com.antmillion.mypage.dto.ExecutedOrdersDTO;
import com.antmillion.mypage.mapper.MyPageMapper;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class MyPageServiceImpl implements MyPageService {

    private final MyPageMapper myPageMapper;
    private final KisApiService kisApiService;
    
    //1. 주식잔고
    @Override
    public List<Map<String, Object>> getStockHoldings(Long accountId) {
        // 1. DB에서 기본 데이터(수량, 매수금액 등) 가져오기
        List<Map<String, Object>> holdings = myPageMapper.selectStockHoldings(accountId);
        
        // 2. 각 종목별로 현재가(고정값) 및 계산 데이터 주입
        for (Map<String, Object> stock : holdings) {
            String stockCode = stock.get("code").toString();
            long shares = ((Number) stock.get("shares")).longValue();
            long buyPrice = ((Number) stock.get("buyPrice")).longValue();
            //한투에서 현재가 조회
            CurrentPriceRequest request = CurrentPriceRequest.builder()
                    .marketCode("J")
                    .stockCode(stockCode)
                    .build();
            long currentPrice = kisApiService.getCurrentPrice(request);
            long totalValue = currentPrice * shares;
            long profit = totalValue - buyPrice;
            
            double profitRate = 0.0;
            if (buyPrice > 0) {
                profitRate = Math.round(((double) profit / buyPrice * 100) * 100) / 100.0;
            }

            // Map에 계산된 값들을 강제로 넣어줌 (JS에서 사용할 키값들)
            stock.put("currentPrice", currentPrice);
            stock.put("totalValue", totalValue);
            stock.put("profit", profit);
            stock.put("profitRate", profitRate);
        }
        
        // 3. (선택사항) totalValue 기준 내림차순 정렬
        holdings.sort((a, b) -> Long.compare((long)b.get("totalValue"), (long)a.get("totalValue")));
        
        return holdings;
    }
    
    //2. 실현손익
    @Override
    public List<Map<String, Object>> getRealizedProfit(Long accountId, String startDate, String endDate) {
        // 매퍼에 넘길 파라미터 맵 생성 (또는 DTO 사용)
        Map<String, Object> params = new HashMap<>();
        params.put("accountId", accountId);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        
        return myPageMapper.selectRealizedProfit(params);
    }
    
    @Override
    public List<ExecutedOrdersDTO> getExecutedOrders(Long accountId, String startDate, String endDate) {
        // Mapper의 select id인 selectExecutedOrders를 호출
        return myPageMapper.selectExecutedOrders(accountId, startDate, endDate);
    }
    
    
    
    //4. 계좌정보
    @Override
    public Map<String, Object> getAccountInfo(Long accountId) {
        return myPageMapper.selectAccountInfo(accountId);
    }
}