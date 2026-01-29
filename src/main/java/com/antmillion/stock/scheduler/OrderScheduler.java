package com.antmillion.stock.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.antmillion.stock.mapper.StockOrderMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderScheduler {
    private final StockOrderMapper stockOrderMapper;

    // 매일 평일 오전 8시 55분에 실행
    @Scheduled(cron = "0 55 8 * * MON-FRI")
    @Transactional
    public void cancelOldOrders() {
        // WAIT, PARTIAL 상태인 모든 주문을 CANCEL로 변경
        int count = stockOrderMapper.cancelRemainingOrders();
        System.out.println("장 시작 전 미체결 주문 {}건을 자동 취소 처리했습니다.");
    }
}
