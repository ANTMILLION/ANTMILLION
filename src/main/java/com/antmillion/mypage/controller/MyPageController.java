package com.antmillion.mypage.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.antmillion.auth.mapper.AccountMapper;
import com.antmillion.mypage.dto.AccountInfoDTO;
import com.antmillion.mypage.dto.ExecutedOrdersDTO;
import com.antmillion.mypage.dto.RealizedProfitDTO;
import com.antmillion.mypage.dto.StockHoldingsDTO;
import com.antmillion.mypage.service.MyPageService;
import com.antmillion.user.dto.AccountDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping({"/mypage"})
@Controller
public class MyPageController {
   
    private final MyPageService myPageService;
    private final AccountMapper accountMapper;

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
    	Long accountId = currentAccountId();

        List<StockHoldingsDTO> holdings = myPageService.getStockHoldings(accountId);
        return ResponseEntity.ok(holdings);
    }
    
    //2. 실현손익
    @GetMapping("/api/realized-profit")
    @ResponseBody
    public ResponseEntity<List<RealizedProfitDTO>> getRealizedProfit(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate
            ) {
        
    	Long accountId = currentAccountId();
        
        List<RealizedProfitDTO> result = myPageService.getRealizedProfit(accountId, startDate, endDate);
        return ResponseEntity.ok(result);
    }
    
    //3. 체결내역
    @GetMapping("/api/executed-orders")
    @ResponseBody
    public ResponseEntity<List<ExecutedOrdersDTO>> getExecutedOrders(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) 
    {
        
    	Long accountId = currentAccountId();

        // 서비스 호출
        List<ExecutedOrdersDTO> list = myPageService.getExecutedOrders(accountId, startDate, endDate);
        return ResponseEntity.ok(list);
    }
    
    //4. 계좌정보
    @GetMapping("/api/account-info")
    @ResponseBody
    public ResponseEntity<AccountInfoDTO> getAccountInfo() {
    	Long accountId = currentAccountId();

        AccountInfoDTO accountInfo = myPageService.getAccountInfo(accountId);
        return ResponseEntity.ok(accountInfo);
    }
    
    private Long currentAccountId() {
        Long userId = currentUserId();
        if (userId == null) return null;

        AccountDTO account = accountMapper.selectByUserId(userId);
        return (account == null) ? null : account.getAccountId();
    }

    private static Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        try {
            return Long.valueOf(auth.getPrincipal().toString());
        } catch (Exception e) {
            return null;
        }
    }
    
}