package com.antmillion.news.service;

import com.antmillion.auth.mapper.MemberMapper;
import com.antmillion.news.dto.NewsLogDTO;
import com.antmillion.news.dto.NewsProgressResponseDTO;
import com.antmillion.news.mapper.NewsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private static final int PROGRESS_PER_NEWS = 20;
    private static final int MAX_PROGRESS = 100;

    private final NewsMapper newsMapper;
    private final MemberMapper memberMapper;

    // 뉴스 읽기 처리 및 포인트 획득 결과 반환
    @Transactional
    public NewsProgressResponseDTO readNews(Long userId, String newsUrl) {
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

        // 현재 상태 조회 (DTO 반환됨)
        NewsProgressResponseDTO currentStatus = getNewsProgress(userId);

        // 기존 상태에 earnedPoint만 추가해서
        return NewsProgressResponseDTO.builder()
                .readCount(currentStatus.getReadCount())
                .progress(currentStatus.getProgress())
                .earnedPoint(earnedPoint) // 포인트 정보 추가
                .build();
    }

    // 현재 뉴스 달성률 조회 (페이지 로딩용)
    public NewsProgressResponseDTO getNewsProgress(Long userId) {
        int count = newsMapper.countTodayNewsRead(userId);

        // 1개당 20%
        int progress = Math.min(count * PROGRESS_PER_NEWS, MAX_PROGRESS);

        return NewsProgressResponseDTO.builder()
                .readCount(count)
                .progress(progress)
                .build();
    }

    @Override
    public List<String> selectTodayReadUrls(Long userId) {
        return newsMapper.selectTodayReadUrls(userId);
    }
}
