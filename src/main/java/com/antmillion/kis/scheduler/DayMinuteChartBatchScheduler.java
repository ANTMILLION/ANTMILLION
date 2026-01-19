// 주식당일분봉조회-차트당일분봉
package com.antmillion.kis.scheduler;

import java.time.LocalTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.antmillion.kis.config.StockConfig;
import com.antmillion.kis.service.DayMinuteCollectorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class DayMinuteChartBatchScheduler {
	private final DayMinuteCollectorService stockCollectorService;
	
	// 평일 9시~15시 40분까지, 매 분 0초에 시작
	@Scheduled(cron = "0 * 9-15 * * MON-FRI")
    public void collectStockData() {
        log.info("주식 데이터 수집 시작 시간: {}", LocalTime.now()); // HHmmss만 필요하기 때문에 LocalTime 사용함
        for (String stockCode : StockConfig.TARGET_STOCKS) {
            try {
                // 데이터 수집 및 Redis 저장 로직 호출
            	stockCollectorService.fetchAndSaveMinuteData(stockCode);
                
                log.info("수집 완료: {} - {}", stockCode, LocalTime.now());

                // 0.6초 대기 (모의투자 1초당 2건 제한 준수)
                Thread.sleep(600); 
                
            } catch (InterruptedException e) {
                log.error("스케줄러 작업 중단됨: {}", e.getMessage());
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("종목 코드 {} 수집 중 에러 발생: {}", stockCode, e.getMessage());
            }
        }
        log.info("전체 수집 종료 시간: {}", LocalTime.now());
    }
}
