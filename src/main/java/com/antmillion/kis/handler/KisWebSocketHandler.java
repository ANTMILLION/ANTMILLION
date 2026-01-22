package com.antmillion.kis.handler;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.antmillion.kis.manager.KisWebSocketManager;

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
	
	public KisWebSocketHandler(KisWebSocketManager manager, String approvalKey, SimpMessagingTemplate messagingTemplate) {
        this.manager = manager;
        this.approvalKey = approvalKey;
        this.messagingTemplate = messagingTemplate;
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
        
        if (payload.contains("PINGPONG")) {
        	manager.updateLastHeartbeatTime(); // 수신 시간 갱신
            System.out.println("PINGPONG 수신 완료");
            return;
        }
        
        /** TR_ID로 구분
         * 실시간 체결 데이터 처리 
         * 실시간 호가 데이터 처리
         */
        
        // 실시간 데이터 파싱 및 STOMP 전송 로직
        System.out.println("수신 데이터: " + payload);
    }
    
    private void sendSubscribeMessage(WebSocketSession session) throws Exception {
    	// 역할: 구독 요청 (Subscribe)
    	// header: approval_key, content-type, body: tr_id, tr_key 등 필요
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
