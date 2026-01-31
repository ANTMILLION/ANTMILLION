package com.antmillion.naver.controller;

import com.antmillion.mission.service.MissionService;
import com.antmillion.naver.dto.PagedNewsResponseDTO;
import com.antmillion.naver.service.NaverNewsService;
import com.antmillion.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/naver/news")
public class NaverNewsController {

    private final NaverNewsService naverNewsService;
    private final NewsService newsService;
    private final MissionService missionService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getNews (
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        // 네이버 뉴스 가져오기
        PagedNewsResponseDTO newsResponse = naverNewsService.getNews(page, size);

        // 사용자 아이디 가져오기
        Long userId = missionService.getCurrentUserId();

        // 오늘 읽은 URL 목록 가져오기 (비로그인이면 빈 리스트)
        List<String> readList = (userId != null) ? newsService.selectTodayReadUrls(userId) : new ArrayList<>();

        // 합쳐서 보내기
        Map<String, Object> response = new HashMap<>();
        response.put("articles", newsResponse.getArticles()); // 네이버 뉴스 리스트
        response.put("totalCount", newsResponse.getTotalCount()); // 전체 뉴스 개수
        response.put("currentPage", newsResponse.getCurrentPage()); // 조회중인 페이지 번호
        response.put("maxPage", newsResponse.getMaxPage());   // 마지막 페이지 번호
        response.put("readList", readList);                   // 내가 읽은 URL 리스트 (String 배열)
        return ResponseEntity.ok(response);
    }
}