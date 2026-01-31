package com.antmillion.naver.controller;

import com.antmillion.naver.dto.NaverNewsListResponseDTO;
import com.antmillion.naver.dto.PagedNewsResponseDTO;
import com.antmillion.naver.service.NaverNewsService;
import com.antmillion.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/naver/news")
public class NaverNewsController {

    private final NaverNewsService naverNewsService;
    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<NaverNewsListResponseDTO> getNews (
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        // 네이버 뉴스 가져오기
        PagedNewsResponseDTO newsResponse = naverNewsService.getNews(page, size);

        // 사용자 아이디 가져오기
        Long userId = currentUserId();

        // 오늘 읽은 URL 목록 가져오기 (비로그인이면 빈 리스트)
        List<String> readList = (userId != null) ? newsService.selectTodayReadUrls(userId) : new ArrayList<>();

        // 응답 DTO 생성 (뉴스 데이터 + 읽은 목록 합치기)
        NaverNewsListResponseDTO response = NaverNewsListResponseDTO.builder()
                .articles(newsResponse.getArticles())
                .totalCount(newsResponse.getTotalCount())
                .currentPage(newsResponse.getCurrentPage())
                .maxPage(newsResponse.getMaxPage())
                .readList(readList) // 여기에 읽은 목록 추가!
                .build();
        return ResponseEntity.ok(response);
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