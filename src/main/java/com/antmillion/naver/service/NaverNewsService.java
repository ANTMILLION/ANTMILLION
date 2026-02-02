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

    private static final DateTimeFormatter NAVER_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

    // 서버 시작 시 초기화
    @PostConstruct
    public void init() {
        initNews();
    }

    private void initNews() {
        List<NaverNewsItemDTO> fetched = fetchFromNaverNewsApi(50);
        newsList.clear();
        List<NaverNewsItemDTO> initial = fetched.stream()
                .limit(5)
                .collect(Collectors.toList());
        newsList.addAll(initial);

        if (initial.isEmpty()) {
            log.info("초기화될 뉴스가 없습니다.");
        } else {
            log.info("처음 뉴스 {}개 조회", initial.size());
        }
    }

    // 1시간마다 최신 2개 추가
    @Scheduled(initialDelay = 60 * 60 * 1000, fixedRate = 60 * 60 * 1000)
    public void appendHourlyNews() {
        // 네이버 API 호출
        List<NaverNewsItemDTO> latest = fetchFromNaverNewsApi(100);
        if (latest.isEmpty()) {
            log.info("네이버에서 가져온 뉴스가 없습니다.");
            return;
        }

        synchronized (newsList) {
            // 기존 링크 스냅샷 및 현재 저장된 가장 최신 시간 계산
            Set<String> existingLinks = newsList.stream()
                    .map(NaverNewsItemDTO::getLink)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            Optional<ZonedDateTime> currentNewest = newsList.stream()
                    .map(NaverNewsItemDTO::getPubDate)
                    .filter(Objects::nonNull)
                    .map(s -> parsePubDate(s, NAVER_DATE_FORMATTER))
                    .filter(Objects::nonNull)
                    .max(Comparator.naturalOrder());

            // latest에서 링크 중복 제거 + pubDate가 현재 저장된 최신보다 이후인 것만 선택
            List<NaverNewsItemDTO> candidates = latest.stream()
                    .filter(item -> item.getLink() != null && item.getPubDate() != null)
                    .filter(item -> !existingLinks.contains(item.getLink()))
                    .filter(item -> {
                        ZonedDateTime itemDt = parsePubDate(item.getPubDate(), NAVER_DATE_FORMATTER);
                        if (itemDt == null) return false;
                        if (currentNewest.isPresent()) {
                            ZonedDateTime cur = currentNewest.get();
                            return itemDt.isAfter(cur);
                        } else {
                            return true;
                        }
                    })
                    .sorted((a, b) -> {
                        ZonedDateTime da = parsePubDate(a.getPubDate(), NAVER_DATE_FORMATTER);
                        ZonedDateTime db = parsePubDate(b.getPubDate(), NAVER_DATE_FORMATTER);
                        if (da == null || db == null) return 0;
                        return db.compareTo(da); // 최신순
                    })
                    .limit(2)
                    .collect(Collectors.toList());

            if (!candidates.isEmpty()) {
                newsList.addAll(0, candidates);
                log.info("뉴스 {}개가 추가되었습니다. 총 뉴스 {}개", candidates.size(), newsList.size());
                candidates.forEach(it -> log.debug("추가된 뉴스: title='{}' link='{}' pubDate='{}'", it.getTitle(), it.getLink(), it.getPubDate()));
            } else {
                log.info("추가될 뉴스가 없습니다.");
            }
        }
    }

    private ZonedDateTime parsePubDate(String pubDateStr, DateTimeFormatter formatter) {
        if (pubDateStr == null) return null;
        try {
            return ZonedDateTime.parse(pubDateStr, formatter);
        } catch (Exception e) {
            log.debug("pubDate 파싱 실패: {}", pubDateStr);
            return null;
        }
    }

    // 뉴스 조회 (페이지네이션)
    public PagedNewsResponseDTO getNews(int page, int size) {
        synchronized (newsList) {
            newsList.sort((o1, o2) -> {
                try {
                    ZonedDateTime t1 = ZonedDateTime.parse(o1.getPubDate(), NAVER_DATE_FORMATTER);
                    ZonedDateTime t2 = ZonedDateTime.parse(o2.getPubDate(), NAVER_DATE_FORMATTER);
                    return t2.compareTo(t1); // t2가 더 크면(최신이면) 앞으로 -> 내림차순
                } catch (Exception e) {
                    return 0;
                }
            });

            int totalCount = newsList.size();
            int start = (page - 1) * size;

            if (start >= totalCount) {
                return new PagedNewsResponseDTO(Collections.emptyList(), totalCount, page, 0);
            }

            int end = Math.min(start + size, totalCount);
            List<NaverNewsItemDTO> paged = newsList.subList(start, end);
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