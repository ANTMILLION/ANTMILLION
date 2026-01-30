package com.antmillion.history.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        Long userId = currentUserId();
        if (userId == null) {
            // 비로그인: 빈 리스트
            return Collections.emptyList();
        }

        if (startDate != null && endDate != null) {
            return historyService.getHistoryListByDate(userId, startDate, endDate);
        }
        return historyService.getHistoryList(userId);
    }

    // 안 읽은 알림 개수
    @GetMapping("/unread-count")
    public int getUnreadCount() {
        Long userId = currentUserId();
        if (userId == null) return 0;
        return historyService.getUnreadCount(userId);
    }

    // 알림 읽음 처리 (historyId가 내 것일 때만)
    @PostMapping("/mark-read/{historyId}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long historyId) {
        Long userId = currentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        historyService.markAsRead(historyId, userId);
        return ResponseEntity.ok().build();
    }

    // 최신 알림 조회
    @GetMapping("/recent")
    public List<HistoryDTO> getRecentNotifications(
            @RequestParam(required = false, defaultValue = "5") int limit
    ) {
        Long userId = currentUserId();
        if (userId == null) return Collections.emptyList();
        return historyService.getRecentNotifications(userId, limit);
    }

    private static Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        try {
            return Long.valueOf(auth.getPrincipal().toString());
        } catch (Exception e) {
            return null;
        }
    }
}
