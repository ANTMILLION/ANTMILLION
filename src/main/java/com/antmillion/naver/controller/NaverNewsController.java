package com.antmillion.naver.controller;

import com.antmillion.naver.dto.NewsSearchResponseDTO;
import com.antmillion.naver.service.NaverNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class NaverNewsController {

    private final NaverNewsService naverNewsService;

    @GetMapping
    public NewsSearchResponseDTO getNews() {
        return naverNewsService.getNews();
    }
}