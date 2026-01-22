package com.antmillion.mypage.service;

import java.util.List;
import java.util.Map;

public interface MyPageService {
    // 계좌 ID를 기반으로 보유 주식 목록(종목명 포함)을 가져옵니다.
    List<Map<String, Object>> getStockHoldings(Long accountId);
}