package com.antmillion.naver.service;

import com.antmillion.naver.config.NaverNewsApiConfig;
import com.antmillion.naver.dto.NaverNewsItemDTO;
import com.antmillion.naver.dto.NaverNewsSearchResponseDTO;
import com.antmillion.naver.dto.NewsSearchResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NaverNewsService {

    @Qualifier("naverNewsRestTemplate")
    private final RestTemplate restTemplate;
    private final NaverNewsApiConfig naverNewsApiConfig;

    // 캐싱을 위한 변수들
    private NewsSearchResponseDTO cachedNews;
    private LocalDateTime lastFetchTime;
    private static final String FIXED_KEYWORD = "증권";

    public NewsSearchResponseDTO getNews() {
        if (cachedNews == null || lastFetchTime == null || lastFetchTime.isBefore(LocalDateTime.now().minusHours(24))) {
            cachedNews = fetchFromNaverNewsApi(FIXED_KEYWORD);
            lastFetchTime = LocalDateTime.now();
        }
        return cachedNews;
    }

    public NewsSearchResponseDTO fetchFromNaverNewsApi(String keyword) {
        URI uri = UriComponentsBuilder
                .fromUriString(naverNewsApiConfig.getNewsUrl())
                .queryParam("query", keyword)
                .queryParam("display", 50)
                .queryParam("start", 1)
                .queryParam("sort", "date")
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", naverNewsApiConfig.getClientId());
        headers.set("X-Naver-Client-Secret", naverNewsApiConfig.getClientSecret());

        ResponseEntity<NaverNewsSearchResponseDTO> response =
                restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        NaverNewsSearchResponseDTO.class
                );

        NaverNewsSearchResponseDTO naverNewsResponse = response.getBody();
        if (naverNewsResponse == null) {
            throw new IllegalStateException("네이버 뉴스 API 응답이 없습니다.");
        }

        // 네이버 뉴스만 필터
        List<NaverNewsItemDTO> filteredItems =
                naverNewsResponse.getItems().stream()
                        .filter(item ->
                                item.getLink() != null &&
                                        item.getLink().startsWith("https://n.news.naver.com") &&
                                        item.getLink().contains("sid=101")
                        )
                        .limit(5)
                        .collect(Collectors.toList());

        NewsSearchResponseDTO newsSearchResponse = new NewsSearchResponseDTO();
        newsSearchResponse.assignTotalCount(filteredItems.size());
        newsSearchResponse.assignArticles(filteredItems);

        return newsSearchResponse;
    }
}
