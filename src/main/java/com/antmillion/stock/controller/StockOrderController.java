package com.antmillion.stock.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.antmillion.stock.dto.StockOrderDTO;
import com.antmillion.stock.service.StockOrderService;
import com.antmillion.user.dto.AccountDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/stock")
public class StockOrderController {

    private final StockOrderService stockOrderService;

    /**
     * 주식 주문 API
     */
    @PostMapping("/order")
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestBody Map<String, Object> orderRequest,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // ===== 테스트용: 세션 없으면 accountId = 1 사용 =====
            AccountDTO accountDTO = (AccountDTO) session.getAttribute("account");
            Long accountId = (accountDTO != null) ? accountDTO.getAccountId() : 1L;
            
            String stockCode = (String) orderRequest.get("stockCode");
            String transactionType = (String) orderRequest.get("transactionType");
            String orderType = (String) orderRequest.get("orderType");
            Integer quantity = (Integer) orderRequest.get("quantity");
            Integer orderPrice = (Integer) orderRequest.get("orderPrice");
            
            log.info("주문 요청 - accountId: {}, stockCode: {}, type: {}, qty: {}, price: {}", 
                    accountId, stockCode, transactionType, quantity, orderPrice);
            
            // 주문 처리
            boolean result = stockOrderService.createOrder(
                    accountId, stockCode, transactionType, orderType, quantity, orderPrice);
            
            if (result) {
                response.put("success", true);
                response.put("message", "주문이 완료되었습니다.");
                log.info("주문 성공");
            } else {
                response.put("success", false);
                response.put("message", "주문 처리 중 오류가 발생했습니다.");
                log.warn("주문 실패");
            }
            
        } catch (IllegalArgumentException e) {
            log.error("주문 검증 실패: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            log.error("주문 오류", e);
            response.put("success", false);
            response.put("message", "주문 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 주문 대기 API
     */
    @GetMapping("/pending-list")
    public ResponseEntity<List<StockOrderDTO>> getPendingList(HttpSession session) {
        AccountDTO accountDTO = (AccountDTO) session.getAttribute("account");
        Long accountId = (accountDTO != null) ? accountDTO.getAccountId() : 1L;

        List<StockOrderDTO> list = stockOrderService.getWaitOrders(accountId);
        return ResponseEntity.ok(list);
    }
    
    /**
     * 주문 취소 API
     */
    @PostMapping("/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(@RequestBody Map<String, Long> request, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        AccountDTO accountDTO = (AccountDTO) session.getAttribute("account");
        Long accountId = (accountDTO != null) ? accountDTO.getAccountId() : 1L;
        Long orderId = request.get("orderId");

        boolean success = stockOrderService.cancelOrder(orderId, accountId);
        
        response.put("success", success);
        response.put("message", success ? "주문이 취소되었습니다." : "취소 가능한 상태가 아닙니다.");
        return ResponseEntity.ok(response);
    }
    
}