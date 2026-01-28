package com.antmillion.naver.controller;

import com.antmillion.mission.service.MissionService;
import com.antmillion.naver.dto.NewsSearchResponseDTO;
import com.antmillion.naver.service.NaverNewsService;
import com.antmillion.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class NaverNewsController {

    private final NaverNewsService naverNewsService;
    private final NewsService newsService;
    private final MissionService missionService;

    @GetMapping
    public Map<String, Object> getNews() {
        // 네이버 뉴스 가져오기
        NewsSearchResponseDTO newsResponse = naverNewsService.getNews();

        // 사용자 아이디 가져오기
        Long userId = missionService.getCurrentUserId();

        // 오늘 읽은 URL 목록 가져오기 (비로그인이면 빈 리스트)
        List<String> readList = (userId != null) ? newsService.selectTodayReadUrls(userId) : new ArrayList<>();

        // 합쳐서 보내기
        Map<String, Object> response = new HashMap<>();
        response.put("articles", newsResponse.getArticles()); // 네이버 뉴스 리스트
        response.put("readList", readList);                   // 내가 읽은 URL 리스트 (String 배열)
        return response;
    }

    // 뉴스 읽기 및 포인트 적립
    @PostMapping("/read")
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
    @GetMapping("/progress")
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