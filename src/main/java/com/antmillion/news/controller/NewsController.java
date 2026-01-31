package com.antmillion.news.controller;

import com.antmillion.mission.service.MissionService;
import com.antmillion.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;
    private final MissionService missionService;

    @GetMapping("/news")
    public String newsPage() {
        return "news/news";
    }

    // 뉴스 읽기 및 포인트 적립
    @PostMapping("/api/mission/news/read")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> readNews(@RequestBody Map<String, String> payload) {
        Long userId = missionService.getCurrentUserId();

        // 비로그인 상태면 null -> 401 에러 처리
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        String newsUrl = payload.get("newsUrl");
        Map<String, Object> result = newsService.readNews(userId, newsUrl);
        return ResponseEntity.ok(result);
    }

    // 현재 달성률 조회
    @GetMapping("/api/mission/news/progress")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProgress() {
        Long userId = missionService.getCurrentUserId();

        // 비로그인 상태면 0%로 응답
        if (userId == null) {
            return ResponseEntity.ok(Map.of("progress", 0, "readCount", 0));
        }

        Map<String, Object> result = newsService.getMissionProgress(userId);
        return ResponseEntity.ok(result);
    }
}