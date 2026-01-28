package com.antmillion.mypage.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.antmillion.mypage.dto.AccountInfoDTO;
import com.antmillion.mypage.dto.ExecutedOrdersDTO;
import com.antmillion.mypage.dto.RealizedProfitDTO;
import com.antmillion.mypage.dto.StockHoldingsDTO;
import com.antmillion.mypage.service.MyPageService;

import lombok.RequiredArgsConstructor;
/* 
	서비스시 수정사항
//실서비스용 추천 코드 패턴
Long accountId = (Long) session.getAttribute("accountId");
if (accountId == null) {
 // 테스트용 코드(accountId = 1L)를 지우고 아래를 활성화
 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
}

*/

@RequiredArgsConstructor
@RequestMapping({"/mypage"})
@Controller
public class MyPageController {
   
    private final MyPageService myPageService;

    //마이페이지 메인 화면 이동
    @GetMapping
    public String mainPage() {
        return "mypage/mypage";
    }


    /**
     * 주식 잔고 데이터를 JSON으로 반환
     */
    //1. 주식잔고
    @GetMapping("/api/stock-holdings")
    @ResponseBody
    public ResponseEntity<List<StockHoldingsDTO>> getStockHoldings(HttpSession session) {
        Long accountId = (Long) session.getAttribute("accountId"); 
        if (accountId == null) accountId = 1L;

        List<StockHoldingsDTO> holdings = myPageService.getStockHoldings(accountId);
        return ResponseEntity.ok(holdings);
    }
    
    //2. 실현손익
    @GetMapping("/api/realized-profit")
    @ResponseBody
    public ResponseEntity<List<RealizedProfitDTO>> getRealizedProfit(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            HttpSession session) {
        
        Long accountId = (Long) session.getAttribute("accountId");
        if (accountId == null) accountId = 1L;
        
        List<RealizedProfitDTO> result = myPageService.getRealizedProfit(accountId, startDate, endDate);
        return ResponseEntity.ok(result);
    }
    
    //3. 체결내역
    @GetMapping("/api/executed-orders")
    @ResponseBody
    public ResponseEntity<List<ExecutedOrdersDTO>> getExecutedOrders(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            HttpSession session) {
        
        // 세션에서 계좌 ID 추출
        Long accountId = (Long) session.getAttribute("accountId");
        
		/*
		 * if (accountId == null) { return
		 * ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
		 */
        
     // ✅ 테스트를 위해 세션이 없으면 1번 계좌로 강제 지정
        if (accountId == null) {
            accountId = 1L; 
        }

        // 서비스 호출
        List<ExecutedOrdersDTO> list = myPageService.getExecutedOrders(accountId, startDate, endDate);
        return ResponseEntity.ok(list);
    }
    
    
    
    
    //4. 계좌정보
    @GetMapping("/api/account-info")
    @ResponseBody
    public ResponseEntity<AccountInfoDTO> getAccountInfo(HttpSession session) {
        Long accountId = (Long) session.getAttribute("accountId");
        if (accountId == null) accountId = 1L;

        AccountInfoDTO accountInfo = myPageService.getAccountInfo(accountId);
        return ResponseEntity.ok(accountInfo);
    }
    
    
}