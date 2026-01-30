package com.antmillion.news.service;

import com.antmillion.auth.mapper.MemberMapper;
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
    private final MemberMapper memberMapper;

    // 뉴스 클릭 처리 및 현재 달성률 반환
    @Transactional
    public Map<String, Object> readNews(Long userId, String newsUrl) {
        // 중복 체크 (오늘 이미 읽은 기사인지)
        boolean alreadyRead = newsMapper.existsTodayLog(userId, newsUrl);
        int earnedPoint = 0; // 뉴스 획득 포인트

        if (!alreadyRead) {
            // 안 읽었으면 기록 저장
            NewsLogDTO newsLogDTO = NewsLogDTO.builder()
                    .userId(userId)
                    .newsUrl(newsUrl)
                    .build();
            newsMapper.insertNewsLog(newsLogDTO);

            // 오늘 읽은 뉴스 개수 조회
            int count = newsMapper.countTodayNewsRead(userId);

            // 5회 이하일 때만 포인트 지급
            if (count <= 5) {
                earnedPoint = 100;
                memberMapper.updateUserPoint(userId, earnedPoint);
            }
        }

        // 5. 달성률 계산 및 결과 반환
        Map<String, Object> response = getMissionProgress(userId);
        response.put("earnedPoint", earnedPoint); // 화면에 포인트 적립 알림 띄우기 위해 추가
        return response;
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
