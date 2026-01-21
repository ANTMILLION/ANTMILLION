package com.antmillion.stock.controller;

import com.antmillion.stock.service.InterestService;
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

}
