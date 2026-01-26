package com.antmillion.kis.controller;

import com.antmillion.kis.manager.KisWebSocketManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/kis/websocket")
@RequiredArgsConstructor
public class KisWebSocketController {

    private final KisWebSocketManager kisWebSocketManager;

    /**
     * 단일 종목 구독
     * POST /api/kis/websocket/subscribe/005930
     */
    @PostMapping("/subscribe/{stockCode}")
    public Map<String, Object> subscribe(
            @PathVariable("stockCode") String stockCode,
            @RequestParam("trId") String trId) {
        Map<String, Object> response = new HashMap<>();
        try {
            kisWebSocketManager.subscribe(stockCode, trId);
            Set<String> stocks;
            if (trId.equals("H0STCNT0")) {
                stocks = kisWebSocketManager.getPresentSubscribedStocks();
            } else {
                stocks = kisWebSocketManager.getAskBidSubscribedStocks();
            }
            response.put("success", true);
            response.put("stockCode", stockCode);
            response.put("subscribedStocks", stocks);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "구독 실패: " + e.getMessage());
        }
        return response;
    }

    /**
     * 여러 종목 한번에 구독
     * POST /api/kis/websocket/subscribe-multiple
     * Body: ["005930", "000660", "035720"]
     */
    @PostMapping("/subscribe-multiple")
    public Map<String, Object> subscribeMultiple(
            @RequestBody List<String> stockCodes,
            @RequestParam("trId") String  trId
            ) {
        Map<String, Object> response = new HashMap<>();
        try {
            for (String stockCode : stockCodes) {
                kisWebSocketManager.subscribe(stockCode, trId);
            }
            Set<String> stocks;
            if (trId.equals("H0STCNT0")) {
                stocks = kisWebSocketManager.getPresentSubscribedStocks();
            } else {
                stocks = kisWebSocketManager.getAskBidSubscribedStocks();
            }
            response.put("success", true);
            response.put("count", stockCodes.size());
            response.put("subscribedStocks", stocks);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "구독 실패: " + e.getMessage());
        }
        return response;
    }

    /**
     * 단일 종목 구독 해제
     * DELETE /api/kis/websocket/unsubscribe/005930
     */
    @PostMapping("/unsubscribe/{stockCode}")
    public Map<String, Object> unsubscribe(
            @PathVariable String stockCode,
            @RequestParam("trId") String  trId
            ) {
        Map<String, Object> response = new HashMap<>();
        try {
            kisWebSocketManager.unsubscribe(stockCode, trId);
            Set<String> stocks;
            if (trId.equals("H0STCNT0")) {
                stocks = kisWebSocketManager.getPresentSubscribedStocks();
            } else {
                stocks = kisWebSocketManager.getAskBidSubscribedStocks();
            }
            response.put("success", true);
            response.put("message", "구독 해제: " + stockCode);
            response.put("subscribedStocks", stocks);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "구독 해제 실패: " + e.getMessage());
        }
        return response;
    }

    /**
     * 전체 구독 해제
     * DELETE /api/kis/websocket/unsubscribe-all
     */
    @PostMapping("/unsubscribe-all")
    public Map<String, Object> unsubscribeAll(@RequestParam("trId") String trId) {
        Map<String, Object> response = new HashMap<>();
        try {
            kisWebSocketManager.unsubscribeAll(trId);
            Set<String> stocks;
            if (trId.equals("H0STCNT0")) {
                stocks = kisWebSocketManager.getPresentSubscribedStocks();
            } else {
                stocks = kisWebSocketManager.getAskBidSubscribedStocks();
            }
            response.put("success", true);
            response.put("message", "전체 구독 해제 완료");
            response.put("subscribedStocks", stocks);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "전체 구독 해제 실패: " + e.getMessage());
        }
        return response;
    }

    /**
     * 현재 구독 중인 종목 목록 조회
     * GET /api/kis/websocket/subscribed
     */
    @GetMapping("/subscribed")
    public Map<String, Object> getSubscribedStocks(
            @RequestParam("trId") String trId
    ) {
        Map<String, Object> response = new HashMap<>();
        Set<String> subscribed = trId.equals("H0STCNT0") ?
                kisWebSocketManager.getPresentSubscribedStocks()
                : kisWebSocketManager.getAskBidSubscribedStocks();
        response.put("count", subscribed.size());
        response.put("stocks", subscribed);
        return response;
    }

}
