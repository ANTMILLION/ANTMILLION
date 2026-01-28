package com.antmillion.naver.service;

import com.antmillion.naver.config.NaverNewsApiConfig;
import com.antmillion.naver.dto.NaverNewsItemDTO;
import com.antmillion.naver.dto.NaverNewsSearchResponseDTO;
import com.antmillion.naver.dto.PagedNewsResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverNewsService {

    @Qualifier("naverNewsRestTemplate")
    private final RestTemplate restTemplate;
    private final NaverNewsApiConfig naverNewsApiConfig;
    private static final String FIXED_KEYWORD = "주식 | 코스피 | 코스닥 | 증시 | 증권";

    // 메모리 뉴스 저장소 (최신순)
    private final List<NaverNewsItemDTO> newsList = Collections.synchronizedList(new ArrayList<>());

    // 서버 시작 시 초기화
    @PostConstruct
    public void init() {
        initNews();
    }

    private void initNews() {
        List<NaverNewsItemDTO> fetched = fetchFromNaverNewsApi(50);
        newsList.clear();
        newsList.addAll(
                fetched.stream()
                        .limit(5)
                        .collect(Collectors.toList())
        );
    }

    // 1시간마다 최신 2개 추가
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void appendHourlyNews() {
        if (newsList.isEmpty()) {
            initNews();
            return;
        }

        List<NaverNewsItemDTO> latest = fetchFromNaverNewsApi(100);

        Set<String> existingLinks = newsList.stream()
                .map(NaverNewsItemDTO::getLink)
                .collect(Collectors.toSet());

        List<NaverNewsItemDTO> newItems = latest.stream()
                .filter(item -> !existingLinks.contains(item.getLink()))
                .limit(2)
                .collect(Collectors.toList());

        if (!newItems.isEmpty()) {
            newsList.addAll(0, newItems);
        }
    }

    // 뉴스 조회 (페이지네이션)
    public PagedNewsResponseDTO getNews(int page, int size) {
        synchronized (newsList) {
            // 리스트를 내보내기 전에 최신순으로 정렬
            // 네이버 뉴스 날짜 포맷: Tue, 28 Jan 2026 15:06:00 +0900
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

            newsList.sort((o1, o2) -> {
                try {
                    ZonedDateTime t1 = ZonedDateTime.parse(o1.getPubDate(), formatter);
                    ZonedDateTime t2 = ZonedDateTime.parse(o2.getPubDate(), formatter);
                    return t2.compareTo(t1); // t2가 더 크면(최신이면) 앞으로 -> 내림차순
                } catch (Exception e) {
                    return 0;
                }
            });

            int totalCount = newsList.size();
            int start = (page - 1) * size;
            int end = Math.min(start + size, totalCount);

            List<NaverNewsItemDTO> paged;
            if (start >= totalCount) {
                paged = Collections.emptyList();
            } else {
                paged = new ArrayList<>(newsList.subList(start, end));
            }
            return new PagedNewsResponseDTO(
                    paged,
                    totalCount,
                    page,
                    (int) Math.ceil((double) totalCount / size)
            );
        }
    }

    // 네이버 뉴스 API 호출
    private List<NaverNewsItemDTO> fetchFromNaverNewsApi(int displayCount) {
        URI uri = UriComponentsBuilder
                .fromUriString(naverNewsApiConfig.getNewsUrl())
                .queryParam("query", FIXED_KEYWORD)
                .queryParam("display", displayCount)
                .queryParam("start", 1)
                .queryParam("sort", "date")
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", naverNewsApiConfig.getClientId());
        headers.set("X-Naver-Client-Secret", naverNewsApiConfig.getClientSecret());

        try {
            ResponseEntity<NaverNewsSearchResponseDTO> response =
                    restTemplate.exchange(
                            uri,
                            HttpMethod.GET,
                            new HttpEntity<>(headers),
                            NaverNewsSearchResponseDTO.class
                    );

            if (response.getBody() != null) {
                return response.getBody().getItems().stream()
                        .filter(item ->
                                item.getLink() != null &&
                                        item.getLink().startsWith("https://n.news.naver.com")
                        )
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("네이버 뉴스 API 호출 실패", e);
        }

        return Collections.emptyList();
    }
}
