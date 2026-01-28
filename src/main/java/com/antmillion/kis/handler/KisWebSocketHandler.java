package com.antmillion.kis.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.antmillion.kis.manager.KisWebSocketManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.antmillion.kis.constant.KisWebSocketTrId.isAskBidTrId;
import static com.antmillion.kis.constant.KisWebSocketTrId.isPresentPriceTrId;

/**
 * 웹소켓 통로를 통해 데이터(메시지) 처리
 * 
 * afterConnectionEstablished: KIS 서버에게 구독 메시지 보냄
 * handleTextMessage: KIS 서버가 정보를 보낼 때마다 자동으로 실행되는 메서드
 *
 * KIS 서버 → JSON 메시지 → ObjectMapper → DTO(자바 객체) → 메모리 저장 → STOMP 전송
 */

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
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
    	// 역할: 데이터 수신 응답 및 처리 (OnMessage)
        String payload = message.getPayload();
        // 1. PINGPONG 처리
        if (payload.contains("PINGPONG")) {
        	manager.updateLastHeartbeatTime(); // 수신 시간 갱신
            System.out.println("PINGPONG 수신 완료");
            try {
            	// 한투 가이드: 받은 PINGPONG 메시지를 그대로 다시 보내야 연결이 유지됨
				session.sendMessage(new TextMessage(payload));
//				System.out.println("PINGPONG 응답 완료");
			} catch (IOException e) {
				System.err.println("PINGPONG 응답 전송 실패: " + e.getMessage());
				manager.monitorHealth(); // 재연결
			}
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
            String trId = parts[1];
            if (isPresentPriceTrId(trId)) {
                String[] data = parts[3].split("\\^");
                // [0], [2], [4], [5], [22]
                Map<String, String> tradeData = new HashMap<>();
                tradeData.put("mkscShrnIscd", data[0]); // 종목코드
                tradeData.put("stckPrpr", data[2]);     // 현재가
                tradeData.put("prdySign", data[3]); // 전일 대비 부호
                tradeData.put("prdyVrss", data[4]);	// 전일 대비
                tradeData.put("prdyCtrt", data[5]);     // 대비율
                tradeData.put("shnuRate", data[22]);     // 매수 비율

                String stockCode = data[0];
                // STOMP 전송
                messagingTemplate.convertAndSend("/topic/kis-trade/present" + stockCode, tradeData); // 실시간 체결가
//                System.out.println("체결가 수신: " + tradeData);
            } else if (isAskBidTrId(trId)) {
                String[] data = parts[3].split("\\^");
                Map<String, String> askBidData = new HashMap<>();
                askBidData.put("mkscShrnIscd", data[0]); // 종목코드
                askBidData.put("askP1", data[3]);
                askBidData.put("askP2", data[4]);
                askBidData.put("askP3", data[5]);
                askBidData.put("askP4", data[6]);
                askBidData.put("askP5", data[7]);
                askBidData.put("bidP1", data[13]);
                askBidData.put("bidP2", data[14]);
                askBidData.put("bidP3", data[15]);
                askBidData.put("bidP4", data[16]);
                askBidData.put("bidP5", data[17]);
                askBidData.put("askPrsqn1", data[23]);
                askBidData.put("askPrsqn2", data[24]);
                askBidData.put("askPrsqn3", data[25]);
                askBidData.put("askPrsqn4", data[26]);
                askBidData.put("askPrsqn5", data[27]);
                askBidData.put("bidPrsqn1", data[33]);
                askBidData.put("bidPrsqn2", data[34]);
                askBidData.put("bidPrsqn3", data[35]);
                askBidData.put("bidPrsqn4", data[36]);
                askBidData.put("bidPrsqn5", data[37]);

                String stockCode = data[0];
                messagingTemplate.convertAndSend("/topic/kis-trade/ask-bid" + stockCode, askBidData);
//                System.out.println("호가 수신: " +  askBidData);
            } else {
                System.out.println("현재가/호가 아님");
            }
        } catch (Exception e) {
            System.err.println("데이터 처리 오류: " + e.getMessage());
        }
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
