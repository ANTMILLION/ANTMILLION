package com.antmillion.news.controller;

import com.antmillion.news.dto.NewsProgressResponseDTO;
import com.antmillion.news.dto.NewsRewardResponseDTO;
import com.antmillion.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/news")
    public String newsPage() {
        return "news/news";
    }

    // 뉴스 읽기 및 포인트 적립
    @PostMapping("/api/mission/news/read")
    @ResponseBody
    public NewsRewardResponseDTO readNews(@RequestBody Map<String, String> payload) {
        Long userId = currentUserId();

        if (userId == null) {
            return NewsRewardResponseDTO.builder()
                    .progress(0)
                    .earnedPoint(0)
                    .build();
        }

        String newsUrl = payload.get("newsUrl");
        return newsService.readNews(userId, newsUrl);
    }

    // 현재 달성률 조회
    @GetMapping("/api/mission/news/progress")
    @ResponseBody
    public NewsProgressResponseDTO getNewsProgress() {
        Long userId = currentUserId();

        if (userId == null) {
            return NewsProgressResponseDTO.builder()
                    .progress(0)
                    .readCount(0)
                    .build();
        }
        return newsService.getNewsProgress(userId);
    }

    private Long currentUserId() {
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