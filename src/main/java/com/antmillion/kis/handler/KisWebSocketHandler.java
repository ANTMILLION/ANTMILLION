package com.antmillion.kis.handler;

import static com.antmillion.kis.constant.KisWebSocketTrId.isAskBidTrId;
import static com.antmillion.kis.constant.KisWebSocketTrId.isPresentPriceTrId;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.antmillion.kis.manager.KisWebSocketManager;
import com.antmillion.stock.service.TradingEngineService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 웹소켓 통로를 통해 데이터(메시지) 처리
 * 
 * afterConnectionEstablished: KIS 서버에게 구독 메시지 보냄 
 * handleTextMessage: KIS 서버가 정보를
 * 보낼 때마다 자동으로 실행되는 메서드
 *
 * KIS 서버 → JSON 메시지 → ObjectMapper → DTO(자바 객체) → 메모리 저장 → STOMP 전송
 */

@Slf4j
public class KisWebSocketHandler extends TextWebSocketHandler {
	private final KisWebSocketManager manager;
	private final String approvalKey;
	private final SimpMessagingTemplate messagingTemplate; // STOMP 전송용
	private ObjectMapper objectMapper;
	private final TradingEngineService tradingEngineService;

	public KisWebSocketHandler(KisWebSocketManager manager, String approvalKey, SimpMessagingTemplate messagingTemplate,
			ObjectMapper objectMapper, TradingEngineService tradingEngineService) {
		this.manager = manager;
		this.approvalKey = approvalKey;
		this.messagingTemplate = messagingTemplate;
		this.objectMapper = objectMapper;
		this.tradingEngineService = tradingEngineService;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.info("### [KIS 웹소켓] 핸드셰이크 성공 및 연결 완료 ###");
	}

	// 숫자를 안전하게 추출하는 헬퍼 메서드 (클래스 내부에 추가)
	private int parseSafeInt(String val) {
		if (val == null || val.trim().isEmpty())
			return 0;
		try {
			// 공백 제거 및 숫자가 아닌 문자 제거 후 파싱
			return Integer.parseInt(val.trim());
		} catch (NumberFormatException e) {
			log.warn("숫자 파싱 실패: [{}]", val);
			return 0;
		}
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) {
		// 역할: 데이터 수신 응답 및 처리 (OnMessage)
		String payload = message.getPayload();
		// 1. PINGPONG 처리
		if (payload.contains("PINGPONG")) {
			manager.updateLastHeartbeatTime(); // 수신 시간 갱신
			log.info("### PINGPONG 수신 완료 ###");
			try {
				// 한투 가이드: 받은 PINGPONG 메시지를 그대로 다시 보내야 연결이 유지됨
				session.sendMessage(new TextMessage(payload));
			} catch (IOException e) {
				System.err.println("PINGPONG 응답 전송 실패: " + e.getMessage());
				manager.monitorHealth(); // 재연결
			}
			return;
		}

		// 2. JSON 응답 처리 (최초 구독 성공 알림 등)
		if (payload.startsWith("{")) {
			return;
		}

		// 3. 실시간 실무 데이터 처리 (문자열 파싱)
		try {
			String[] parts = payload.split("\\|");
			String trId = parts[1];
			if (isPresentPriceTrId(trId)) {
				String[] data = parts[3].split("\\^");
				if (data[2].equals("0")) {
		               return;
		            }
				// [0], [2], [4], [5], [22]
				Map<String, String> tradeData = new HashMap<>();
				tradeData.put("mkscShrnIscd", data[0]); // 종목코드
				tradeData.put("stckPrpr", data[2]); // 현재가
				tradeData.put("prdySign", data[3]); // 전일 대비 부호
				tradeData.put("prdyVrss", data[4]); // 전일 대비
				tradeData.put("prdyCtrt", data[5]); // 대비율
				tradeData.put("shnuRate", data[22]); // 매수 비율

				String stockCode = data[0];

				// STOMP 전송
				messagingTemplate.convertAndSend("/topic/kis-trade/present" + stockCode, tradeData); // 실시간 체결가
				log.info("### [실시간 체결가] 종목: {} ###\n{}", stockCode, tradeData);
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
				log.info("### [실시간 호가] 종목: {} ###\n{}", stockCode, askBidData);
				int bestBidPrice = parseSafeInt(data[13]); // 매수 1호가
				int bestBidQty = parseSafeInt(data[33]); // 매수 1호가 잔량

				int bestAskPrice = parseSafeInt(data[3]); // 매도 1호가
				int bestAskQty = parseSafeInt(data[23]); // 매도 1호가 잔량
				// 매수 주문 체결 확인 (최우선 매도호가에 내 매수 주문이 닿았는가)
				tradingEngineService.processExecution(stockCode, bestAskPrice, bestAskQty);
				// 매도 주문 체결 확인 (최우선 매수호가에 내 매도 주문이 닿았는가)
				tradingEngineService.processExecution(stockCode, bestBidPrice, bestBidQty);
			} else {
				log.info("### [알림] 현재가/호가 데이터 아님 ###");
			}
		} catch (Exception e) {
			log.error("### [ERROR] 데이터 처리 오류: {} ###", e.getMessage(), e);
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		log.error("### [ERROR] 웹소켓 통신 에러: {} ###", exception.getMessage());
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		log.info("### KIS 연결 종료: {} ###", status.getReason());
	}

}
