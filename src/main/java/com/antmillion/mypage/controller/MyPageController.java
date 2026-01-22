package com.antmillion.mypage.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.antmillion.mypage.service.MyPageService;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@RequestMapping({"/mypage"})
@Controller
public class MyPageController {
   
    private final MyPageService myPageService;

    /**
     * 마이페이지 메인 화면 이동
     */
    @GetMapping
    public String mainPage() {
        return "mypage/mypage";
    }
    
    
    
    
    @GetMapping("/api/realized-profit")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getRealizedProfit(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            HttpSession session) {
        
        Long accountId = (Long) session.getAttribute("accountId");
        if (accountId == null) accountId = 1L;
        
        // 서비스로 날짜 데이터 전달
        List<Map<String, Object>> result = myPageService.getRealizedProfit(accountId, startDate, endDate);
        return ResponseEntity.ok(result);
    }

    /**
     * 주식 잔고 데이터를 JSON으로 반환
     */
    @GetMapping("/api/stock-holdings")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getStockHoldings(HttpSession session) {
        // 1. 세션에서 로그인된 사용자 계좌 ID 가져오기
        // (로그인 시 세션에 "accountId"가 저장되어 있다고 가정합니다)
        Long accountId = (Long) session.getAttribute("accountId"); 
        
        if (accountId == null) {
            // 로그인 정보가 없을 경우 처리 (예: 테스트용 ID 1L 사용 또는 에러 처리)
            accountId = 1L; 
        }

        // 2. 서비스를 통해 실제 DB 데이터 조회
        List<Map<String, Object>> holdings = myPageService.getStockHoldings(accountId);
        
        return ResponseEntity.ok(holdings);
    }
}