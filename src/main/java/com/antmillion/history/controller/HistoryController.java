package com.antmillion.history.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.antmillion.history.dto.HistoryDTO;
import com.antmillion.history.service.HistoryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/history")
public class HistoryController {
	
	 private final HistoryService historyService;

    @GetMapping("/list")
    public List<HistoryDTO> getHistoryList(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        Long userId = 1L;

        // 날짜가 있으면 날짜 기준 조회
        if (startDate != null && endDate != null) {
            return historyService.getHistoryListByDate(userId, startDate, endDate);
        }

        // 날짜 없으면 기존 전체 조회
        return historyService.getHistoryList(userId);
    }
    // 안 읽은 알림 개수 조회
    @GetMapping("/unread-count")
    public int getUnreadCount(@RequestParam(required = false, defaultValue = "1") Long userId) {
        return historyService.getUnreadCount(userId);
    }

    // 알림 읽음 처리
    @PostMapping("/mark-read/{historyId}")
    public void markAsRead(@PathVariable Long historyId) {
        historyService.markAsRead(historyId);
    }
    // 최신 알림 조회 (알림창용, 최대 5개)
    @GetMapping("/recent")
    public List<HistoryDTO> getRecentNotifications(
            @RequestParam(required = false, defaultValue = "1") Long userId,
            @RequestParam(required = false, defaultValue = "5") int limit
    ) {
        return historyService.getRecentNotifications(userId, limit);
    }
}
    
