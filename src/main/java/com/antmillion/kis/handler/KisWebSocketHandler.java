package com.antmillion.kis.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.antmillion.kis.dto.KisWebSocketTransactionPriceRequest;
import com.antmillion.kis.manager.KisWebSocketManager;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 웹소켓 통로를 통해 데이터(메시지) 처리
 * 
 * afterConnectionEstablished: KIS 서버에게 구독 메시지 보냄
 * handleTextMessage: KIS 서버가 정보를 보낼 때마다 자동으로 실행되는 메서드
 *
 * KIS 서버 → JSON 메시지 → ObjectMapper → DTO(자바 객체) → 메모리 저장 → STOMP 전송
 */

@Component
public class KisWebSocketHandler extends TextWebSocketHandler {
	private final KisWebSocketManager manager;
	private final String approvalKey;
	private final SimpMessagingTemplate messagingTemplate; // STOMP 전송용
	private ObjectMapper objectMapper;
	
	public KisWebSocketHandler(KisWebSocketManager manager, String approvalKey, 
            SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
		this.manager = manager;
		this.approvalKey = approvalKey;
		this.messagingTemplate = messagingTemplate;
		this.objectMapper = objectMapper;
	}
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("KIS 웹소켓 핸드셰이크 성공");
        
        sendSubscribeMessage(session); // 실제 구독 메시지 전송
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
    	// 역할: 데이터 수신 응답 및 처리 (OnMessage)
        String payload = message.getPayload();
        
     // 1. PINGPONG 처리
        if (payload.contains("PINGPONG")) {
        	manager.updateLastHeartbeatTime(); // 수신 시간 갱신
            System.out.println("PINGPONG 수신 완료");
            return;
        }
        
        // 2. JSON 응답 처리 (최초 구독 성공 알림 등)
        if (payload.startsWith("{")) {
            System.out.println("시스템 메시지(JSON): " + payload);
            return; 
        }
        
        // 3. 실시간 실무 데이터 처리 (문자열 파싱)
        try {
            String[] parts = payload.split("\\|");
            if (parts.length > 5 && "H0STCNT0".equals(parts[1])) {
            	String[] data = parts[3].split("\\^");
            	// [0], [2], [4], [5], [22]
                Map<String, String> tradeData = new HashMap<>();
                tradeData.put("mkscShrnIscd", data[0]); // 종목코드
                tradeData.put("stckPrpr", data[2]);     // 현재가
                tradeData.put("prdyVrss", data[4]);	// 전일 대비
                tradeData.put("prdyCtrt", data[5]);     // 대비율
                tradeData.put("shnuRate", data[22]);     // 매수 비율
                
                // STOMP 전송
                messagingTemplate.convertAndSend("/topic/kis-trade", tradeData); // 실시간 체결가
                System.out.println("체결가 수신: " + tradeData);
            }
        } catch (Exception e) {
            System.err.println("데이터 처리 오류: " + e.getMessage());
        }
    }

    private void sendSubscribeMessage(WebSocketSession session) throws Exception {
    	// 역할: 구독 요청 (Subscribe)
    	// header: approval_key, content-type, body: tr_id, tr_key 등 필요
    	
    	
    	KisWebSocketTransactionPriceRequest request = KisWebSocketTransactionPriceRequest.builder()
    	        .header(KisWebSocketTransactionPriceRequest.Header.builder()
    	            .approvalKey(approvalKey)
    	            .custtype("P")
    	            .trType("1")
    	            .contentType("utf-8")
    	            .build())
    	        .body(KisWebSocketTransactionPriceRequest.Body.builder()
    	            .input(KisWebSocketTransactionPriceRequest.Body.Input.builder()
    	                .trId("H0STCNT0")   // 실시간 체결가 tr_id
    	                .trKey("005930")
    	                .build())
    	            .build())
    	        .build();

    	    // JSON 변환
    	    String json = objectMapper.writeValueAsString(request);
    	    session.sendMessage(new TextMessage(json));

    	    System.out.println("구독 요청 전송 완료: " + json);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.println("웹소켓 통신 에러: " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("KIS 연결 종료: " + status.getReason());
    }

}
