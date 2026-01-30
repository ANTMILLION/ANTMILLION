package com.antmillion.stock.controller;

import com.antmillion.auth.mapper.AccountMapper;
import com.antmillion.kis.dto.CurrentPrice;
import com.antmillion.kis.service.KisApiService;
import com.antmillion.stock.service.InterestService;
import com.antmillion.stock.service.StockService;

import com.antmillion.user.dto.AccountDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/interest")
@RequiredArgsConstructor
public class InterestController {

    private final InterestService interestService;
    private final KisApiService kisApiService;
    private final StockService stockService;
    private final AccountMapper accountMapper;

    /**
     * 관심종목 토글 (추가/삭제)
     */
    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleInterest(@RequestBody Map<String, String> request) {
        String stockCode = request.get("stockCode");
        
        Long accountId = currentAccountId();
        if (accountId == null) {
            // (주의) token.js가 401을 만나면 /login으로 리다이렉트할 수 있어서
            // 여기서는 200 + 메시지로 내려줌(프론트에서 모달 처리)
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "LOGIN_REQUIRED",
                    "stockCode", stockCode,
                    "isInterest", false
            ));
        }
        
        boolean result = interestService.toggleInterest(stockCode, accountId); //임시로 고정값 사용. 나중에 인증 구현되면 수정
        boolean isInterest = interestService.isInterest(stockCode, accountId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        response.put("isInterest", isInterest);
        response.put("stockCode", stockCode);

        return ResponseEntity.ok(response);
    }

    /**
     * 관심종목 목록 조회 (종목 코드만)
     */
    @GetMapping("/list")
    public ResponseEntity<List<String>> getInterestList() {
    	Long accountId = currentAccountId();
        if (accountId == null) {
            return ResponseEntity.ok(List.of());
        }
    	
        List<String> interestStockCodes = interestService.getInterestStockCodes(accountId);
        return ResponseEntity.ok(interestStockCodes);
    }

    /**
     * 특정 종목이 관심종목인지 확인
     */
    @GetMapping("/check/{stockCode}")
    public ResponseEntity<Map<String, Boolean>> checkInterest(@PathVariable String stockCode) {
    	Long accountId = currentAccountId();
        if (accountId == null) {
            return ResponseEntity.ok(Map.of("isInterest", false));
        }
        boolean isInterest = interestService.isInterest(stockCode, accountId);
        return ResponseEntity.ok(Map.of("isInterest", isInterest));
    }

    // InterestController
    @GetMapping("/details/paged")
    public ResponseEntity<Map<String, Object>> getInterestDetailsPaged(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

    	Long accountId = currentAccountId();

        List<String> allStockCodes = (accountId == null)
                ? List.of()
                : interestService.getInterestStockCodes(accountId);

        if (allStockCodes.isEmpty()) {
            Map<String, Object> emptyResponse = new HashMap<>();
            emptyResponse.put("data", List.of());
            emptyResponse.put("currentPage", page);
            emptyResponse.put("totalItems", 0);
            emptyResponse.put("totalPages", 0);
            return ResponseEntity.ok(emptyResponse);
        }

        // 2. 페이징 계산
        int totalItems = allStockCodes.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, totalItems);

        // 3. 현재 페이지 종목 코드만 추출
        List<String> pagedStockCodes = allStockCodes.subList(startIndex, endIndex);

        // 4. 페이징된 종목들만 API 조회
        List<CurrentPrice> pagedPrices = kisApiService.getCurrentPricesDetail(pagedStockCodes);

        // 5. stockCode 세팅 (순서 보장)
        for (int i = 0; i < pagedPrices.size() && i < pagedStockCodes.size(); i++) {
            String stockCode = pagedStockCodes.get(i);
            pagedPrices.get(i).setStockCode(stockCode);
            pagedPrices.get(i).setStockName(stockService.getStockByCode(stockCode).getStockName());
        }

        // 6. 응답 구성
        Map<String, Object> response = new HashMap<>();
        response.put("data", pagedPrices);
        response.put("currentPage", page);
        response.put("totalItems", totalItems);
        response.put("totalPages", totalPages);

        return ResponseEntity.ok(response);
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
