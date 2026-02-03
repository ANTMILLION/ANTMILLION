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

/**
 * KIS 웹소켓 세션 관리자 (KisWebSocketManager)
 * 1. 연결: connect() 호출 시 KisWebSocketHandler를 등록하고 KIS 서버와 세션 수립
 * 2. 구독 관리: 외부에서 subscribe() 호출 시, 내부 Set에 종목 저장 및 KIS 서버에 구독 JSON 전송
 * 3. 생존 확인: lastHeartbeatTime을 관리하며, Handler에서 호출하는 monitorHealth()를 통해 재연결 수행
 * 4. 세션 공유: 수립된 세션을 통해 Handler와 독립적으로 서버에 메시지(구독/해제)를 보냄
 */

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
            System.out.println("KIS 서버와 웹소켓 연결 완료");
        } catch (Exception e) {
        	isConnected = false;
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
            System.out.println("세션이 끊겼으므로 재연결 시도");
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
            System.out.println("이미 " + trId + " 구독 중인 종목: " + stockCode);
            return;
        }

        if (session == null || !session.isOpen()) {
            System.out.println("웹소켓 세션 연결");
            connect();
        }
        
        this.sendSubscribeMessage(this.session, stockCode, "1", trId);  // 실제 구독 메시지 전송
        targetSet.add(stockCode);

        System.out.println("구독 완료: " + stockCode + " trId: " + trId);
    }

    // 구독 해제 메소드
    public synchronized void unsubscribe(String stockCode, String trId) {
        Set<String> targetSet = isPresentPriceTrId(trId) ?  presentSubscribedStocks : askBidSubscribedStocks;
        if (!targetSet.contains(stockCode)) {
            System.out.println(trId + " 구독 중이 아닌 종목: " + stockCode);
            return;
        }

        if (this.session != null && this.session.isOpen()) {
            sendSubscribeMessage(this.session, stockCode, "2", trId);  // "2" = 구독 해제
            targetSet.remove(stockCode);
            System.out.println("구독 해제: " + stockCode + " trId: " + trId);
        }
    }

    // 전체 구독 해제
    public synchronized void unsubscribeAll(String trId) {
        Set<String> targetSet = isPresentPriceTrId(trId) ?  presentSubscribedStocks : askBidSubscribedStocks;
        if (targetSet.isEmpty()) {
            System.out.println(trId + " 구독 중인 종목이 없습니다.");
            return;
        }

        System.out.println(trId + " 전체 구독 해제 시작");
        // 복사본으로 반복 (ConcurrentModificationException 방지)
        Set<String> stocksToUnsubscribe = ConcurrentHashMap.newKeySet();
        stocksToUnsubscribe.addAll(targetSet);

        for (String stockCode : stocksToUnsubscribe) {
            unsubscribe(stockCode, trId);
        }

        System.out.println(trId + " 전체 구독 해제 완료");
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
            System.out.println(dataType + (trType.equals("1") ? "구독" : "해제") + " 요청 전송: " + stockCode);
        } catch (IOException e) {
            System.err.println("메시지 전송 실패: " + e.getMessage());
        }
    }
}
