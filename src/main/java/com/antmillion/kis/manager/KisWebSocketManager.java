package com.antmillion.kis.manager;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import com.antmillion.kis.handler.KisWebSocketHandler;
import com.antmillion.kis.service.KisApiService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 웹소켓 Connection 처리
 * 
 * KisWebSocketManager → 세션 연결 → KisWebSocketHandler → 구독 요청 → KIS 서버에서 handleTextMessage로 실시간 데이터 전송함
 * 세션이 끊기면 KisWebSocketManager가 connect() 시도
 *
 */

@Service
public class KisWebSocketManager {
    private final KisApiService kisApiService;
    private final SimpMessagingTemplate messagingTemplate; // STOMP 전송용 주입
    private WebSocketSession session;
    private long lastHeartbeatTime;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String WS_URL = "ws://ops.koreainvestment.com:21000"; // 실전투자 기준
    private boolean isConnected = false; // 연결 상태 저장

    public KisWebSocketManager(KisApiService kisApiService, SimpMessagingTemplate messagingTemplate) {
        this.kisApiService = kisApiService;
        this.messagingTemplate = messagingTemplate;
    }
    
    @PostConstruct
    public void init() {
    	connect();
    }

	// 웹소켓 연결
    public void connect() {
        if (session != null && session.isOpen()) return;

        try {
            String approvalKey = kisApiService.getKisApprovalKey();
            KisWebSocketHandler handler = new KisWebSocketHandler(this, approvalKey, messagingTemplate, objectMapper);
            StandardWebSocketClient client = new StandardWebSocketClient();
            
            session = client.doHandshake(handler, WS_URL).get();   
            lastHeartbeatTime = System.currentTimeMillis();
            System.out.println("KIS 서버와 웹소켓 연결 완료");
            isConnected = true;
        } catch (Exception e) {
            System.err.println("웹소켓 연결 실패: " + e.getMessage());
        }
    }
    
	 // 브라우저 종료로 인한 세션 종료
    public void disconnect() {
        if (session != null && session.isOpen()) {
            try {
                session.close();
                System.out.println("브라우저 종료로 인한 KIS 세션 종료");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.session = null;
        isConnected = false;
    }
    
    // 웹소켓 재연결
    public void monitorHealth() {
        if (session == null || !session.isOpen()) {
            System.out.println("세션 재연결...");
            connect();
            return;
        }
    }
    
    public void setSession(WebSocketSession session) {
        this.session = session;
    }
    
    public WebSocketSession getSession() {
        return session;
    }
    
    public void updateLastHeartbeatTime() {
        this.lastHeartbeatTime = System.currentTimeMillis();
    }
}
