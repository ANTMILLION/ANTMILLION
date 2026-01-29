package com.antmillion.stock.service;

import com.antmillion.kis.dto.CurrentPrice;

import java.util.List;

public interface InterestService {

    // 관심종목 추가
    boolean addInterest(String stockCode, Long accountId);

    // 관심종목 삭제
    boolean removeInterest(String stockCode, Long accountId);

    // 관심종목 토글 (있으면 삭제, 없으면 추가)
    boolean toggleInterest(String stockCode, Long accountId);

    // 특정 계정의 관심종목 코드 목록 조회
    List<String> getInterestStockCodes(Long accountId);

    // 특정 종목이 관심종목인지 확인
    boolean isInterest(String stockCode, Long accountId);

}
