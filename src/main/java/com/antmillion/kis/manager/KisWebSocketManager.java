package com.antmillion.kis.manager;

import static com.antmillion.kis.constant.KisWebSocketTrId.isPresentPriceTrId;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import com.antmillion.kis.dto.KisWebSocketTransactionPriceRequest;
import com.antmillion.kis.handler.KisWebSocketHandler;
import com.antmillion.kis.service.KisApiService;
import com.antmillion.stock.service.TradingEngineService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 웹소켓 Connection 처리
 * 
 * KisWebSocketManager → 세션 연결 → KisWebSocketHandler → 구독 요청 → KIS 서버에서 handleTextMessage로 실시간 데이터 전송함
 * 세션이 끊기면 KisWebSocketManager가 connect() 시도
 *
 */

@Slf4j
@Service
public class KisWebSocketManager {
    private final KisApiService kisApiService;
    private final SimpMessagingTemplate messagingTemplate; // STOMP 전송용 주입
    @Getter @Setter
    private WebSocketSession session;
    private long lastHeartbeatTime;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String WS_URL = "ws://ops.koreainvestment.com:21000"; // 실전투자 기준
    private boolean isConnected = false; // 연결 상태 저장
    private String approvalKey;
    private final TradingEngineService tradingEngineService;

    private final Set<String> presentSubscribedStocks = ConcurrentHashMap.newKeySet();

    private final Set<String> askBidSubscribedStocks = ConcurrentHashMap.newKeySet();

    public KisWebSocketManager(KisApiService kisApiService, SimpMessagingTemplate messagingTemplate, TradingEngineService tradingEngineService) {
        this.kisApiService = kisApiService;
        this.messagingTemplate = messagingTemplate;
		this.tradingEngineService = tradingEngineService;
    }

	// 웹소켓 연결
    public synchronized void connect() {
    	
        if (session != null && session.isOpen()) return;

        try {
        	approvalKey = kisApiService.getKisApprovalKey();
            KisWebSocketHandler handler = new KisWebSocketHandler(this, approvalKey, messagingTemplate, objectMapper, tradingEngineService);
            StandardWebSocketClient client = new StandardWebSocketClient();
            
            session = client.doHandshake(handler, WS_URL).get();   
            lastHeartbeatTime = System.currentTimeMillis();
            isConnected = true;
            log.info("### KIS 서버와 웹소켓 연결 완료 ###");
        } catch (Exception e) {
        	isConnected = false;
        	log.error("### [ERROR] 웹소켓 연결 실패: {} ###", e.getMessage());
        }
    }
    
	 // 브라우저 종료로 인한 세션 종료
    public void disconnect() {
        if (session != null && session.isOpen()) {
            try {
                session.close();
                log.info("### 브라우저 종료로 인한 KIS 세션 종료 ###");
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
        	log.info("### [RECONNECT] 세션 끊김 - 재연결 시도 ###");
            session = null;
            connect();
        }
    }
    
    public void updateLastHeartbeatTime() {
        this.lastHeartbeatTime = System.currentTimeMillis();
    }
    
    // 세션 관리용
    public synchronized void subscribe(String stockCode, String trId) {
        Set<String> targetSet = isPresentPriceTrId(trId) ?  presentSubscribedStocks : askBidSubscribedStocks;
        // 중복 구독 방지
        if (targetSet.contains(stockCode)) {
        	log.info("### [SKIP] 이미 {} 구독 중인 종목: {} ###", trId, stockCode);
            return;
        }

        if (session == null || !session.isOpen()) {
        	log.info("### [KIS 웹소켓] 연결 시도 중... ###");
            connect();
        }
        
        this.sendSubscribeMessage(this.session, stockCode, "1", trId);  // 실제 구독 메시지 전송
        targetSet.add(stockCode);
        log.info("### [SUCCESS] 구독 완료: {} | TR_ID: {} ###", stockCode, trId);
    }

    // 구독 해제 메소드
    public synchronized void unsubscribe(String stockCode, String trId) {
        Set<String> targetSet = isPresentPriceTrId(trId) ?  presentSubscribedStocks : askBidSubscribedStocks;
        if (!targetSet.contains(stockCode)) {
        	log.info("### [INFO] {} 구독 중이 아닌 종목: {} ###", trId, stockCode);
            return;
        }

        if (this.session != null && this.session.isOpen()) {
            sendSubscribeMessage(this.session, stockCode, "2", trId);  // "2" = 구독 해제
            targetSet.remove(stockCode);
            log.info("### [SUCCESS] 구독 해제: {} | TR_ID: {} ###", stockCode, trId);
        }
    }

    // 전체 구독 해제
    public synchronized void unsubscribeAll(String trId) {
        Set<String> targetSet = isPresentPriceTrId(trId) ?  presentSubscribedStocks : askBidSubscribedStocks;
        if (targetSet.isEmpty()) {
        	log.info("### [INFO] {} 구독 중인 종목이 없습니다. ###", trId);
            return;
        }

        log.info("### [PROCESS] {} 전체 구독 해제 시작 ###", trId);
        // 복사본으로 반복 (ConcurrentModificationException 방지)
        Set<String> stocksToUnsubscribe = ConcurrentHashMap.newKeySet();
        stocksToUnsubscribe.addAll(targetSet);

        for (String stockCode : stocksToUnsubscribe) {
            unsubscribe(stockCode, trId);
        }

        log.info("### [SUCCESS] {} 전체 구독 해제 완료 ###", trId);
    }

    // 구독/해제 메시지 전송 (통합)
    private void sendSubscribeMessage(WebSocketSession session, String stockCode, String trType, String trId) {
        KisWebSocketTransactionPriceRequest request = KisWebSocketTransactionPriceRequest.builder()
                .header(KisWebSocketTransactionPriceRequest.Header.builder()
                        .approvalKey(approvalKey)
                        .custtype("P")
                        .trType(trType)  // "1" = 구독, "2" = 해제
                        .contentType("utf-8")
                        .build())
                .body(KisWebSocketTransactionPriceRequest.Body.builder()
                        .input(KisWebSocketTransactionPriceRequest.Body.Input.builder()
                                .trId(trId)
                                .trKey(stockCode)
                                .build())
                        .build())
                .build();

        try {
            String json = objectMapper.writeValueAsString(request);
            session.sendMessage(new TextMessage(json));

            String dataType = isPresentPriceTrId(trId) ? "체결가" : "호가";
            log.info("### [REQUEST] {} {} 요청 전송: {} ###", 
                    dataType, (trType.equals("1") ? "구독" : "해제"), stockCode);
        } catch (IOException e) {
            System.err.println("메시지 전송 실패: " + e.getMessage());
        }
    }
}
