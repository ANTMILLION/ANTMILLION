package com.antmillion.kis.controller;

import com.antmillion.kis.constant.KisWebSocketTrId;
import com.antmillion.kis.manager.KisWebSocketManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/kis/websocket")
@RequiredArgsConstructor
public class KisWebSocketController {

    private final KisWebSocketManager kisWebSocketManager;

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
            String actualTrId;
            if (trId.contains("CNT")) {
                actualTrId = KisWebSocketTrId.getCurrentPresentTrId();
            } else {
                actualTrId = KisWebSocketTrId.getCurrentAskBidTrId();
            }
            // 병렬 처리로 변경
            stockCodes.parallelStream().forEach(stockCode -> {
                try {
                    kisWebSocketManager.subscribe(stockCode, actualTrId);
                } catch (Exception e) {
                    log.error("종목 {} 구독 실패: {}", stockCode, e.getMessage());
                }
            });

            response.put("success", true);
            response.put("count", stockCodes.size());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "구독 실패: " + e.getMessage());
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
            String actualTrId;
            if (trId.contains("CNT")) {
                actualTrId = KisWebSocketTrId.getCurrentPresentTrId();
            } else {
                actualTrId = KisWebSocketTrId.getCurrentAskBidTrId();
            }
            kisWebSocketManager.unsubscribeAll(actualTrId);
            response.put("success", true);
            response.put("message", "전체 구독 해제 완료");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "전체 구독 해제 실패: " + e.getMessage());
        }
        return response;
    }

}
