package com.antmillion.stock.controller;

import com.antmillion.kis.dto.CurrentPrice;
import com.antmillion.kis.service.KisApiService;
import com.antmillion.stock.service.InterestService;
import com.antmillion.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//todo: 인증 구현이 아직 안되어서, account_id를 고정값 3L로 주고있음. 나중에 전부 수정
@RestController
@RequestMapping("/api/interest")
@RequiredArgsConstructor
public class InterestController {

    private final InterestService interestService;
    private final KisApiService kisApiService;
    private final StockService stockService;

    /**
     * 관심종목 토글 (추가/삭제)
     */
    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleInterest(@RequestBody Map<String, String> request) {
        String stockCode = request.get("stockCode");

        boolean result = interestService.toggleInterest(stockCode, 3L); //임시로 고정값 사용. 나중에 인증 구현되면 수정
        boolean isInterest = interestService.isInterest(stockCode, 3L);

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
        List<String> interestStockCodes = interestService.getInterestStockCodes(3L);
        return ResponseEntity.ok(interestStockCodes);
    }

    /**
     * 특정 종목이 관심종목인지 확인
     */
    @GetMapping("/check/{stockCode}")
    public ResponseEntity<Map<String, Boolean>> checkInterest(@PathVariable String stockCode) {
        boolean isInterest = interestService.isInterest(stockCode, 3L);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isInterest", isInterest);

        return ResponseEntity.ok(response);
    }

    // InterestController
    @GetMapping("/details/paged")
    public ResponseEntity<Map<String, Object>> getInterestDetailsPaged(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long accountId = 3L;

        // 1. 전체 관심종목 코드 조회
        List<String> allStockCodes = interestService.getInterestStockCodes(accountId);

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

}
