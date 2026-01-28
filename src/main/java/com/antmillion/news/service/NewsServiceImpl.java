package com.antmillion.news.service;

import com.antmillion.news.dto.NewsLogDTO;
import com.antmillion.news.mapper.NewsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsMapper newsMapper;

    // 뉴스 클릭 처리 및 현재 달성률 반환
    @Transactional
    public Map<String, Object> readNews(Long userId, String newsUrl) {
        // 중복 체크 (오늘 이미 읽은 기사인지)
        boolean alreadyRead = newsMapper.existsTodayLog(userId, newsUrl);

        if (!alreadyRead) {
            // 안 읽었으면 기록 저장
            NewsLogDTO newsLogDTO = NewsLogDTO.builder()
                    .userId(userId)
                    .newsUrl(newsUrl)
                    .build();
            newsMapper.insertNewsLog(newsLogDTO);
        }

        // 달성률 계산 (읽은 후 카운트 다시 조회)
        return getMissionProgress(userId);
    }

    // 현재 미션 달성률 조회 (페이지 로딩용)
    public Map<String, Object> getMissionProgress(Long userId) {
        int count = newsMapper.countTodayNewsRead(userId);

        // 1개당 20%
        int progress = Math.min(count * 20, 100);

        Map<String, Object> response = new HashMap<>();
        response.put("readCount", count);     // 읽은 개수
        response.put("progress", progress);   // 달성률 (%)

        return response;
    }

    @Override
    public List<String> selectTodayReadUrls(Long userId) {
        return newsMapper.selectTodayReadUrls(userId);
    }
}
