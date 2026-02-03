package com.antmillion.mission.service;

import com.antmillion.mission.dto.MissionProgressResponseDTO;
import com.antmillion.quiz.mapper.QuizMapper;
import com.antmillion.news.mapper.NewsMapper;
import com.antmillion.user.dto.UserRankResponseDTO;
import com.antmillion.user.service.AntRankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MissionServiceImpl implements MissionService {
    private static final int QUIZ_GOAL = 2;
    private static final int NEWS_GOAL = 5;
    private static final int QUIZ_SCORE_PER_ITEM = 20;
    private static final int NEWS_SCORE_PER_ITEM = 12;
    private static final int MAX_PROGRESS = 100;

    private final QuizMapper quizMapper;
    private final AntRankService antRankService;
    private final NewsMapper newsMapper;

    @Override
    public UserRankResponseDTO getUserRankInfo(Long userId) {
        return antRankService.getUserRankInfo(userId);
    }

    public boolean isTodayMissionCompleted(Long userId) {
        MissionProgressResponseDTO status = getTodayMissionProgress(userId);
        // totalProgress가 100이면 완료로 간주
        return status.getTotalProgress() >= MAX_PROGRESS;
    }

    public MissionProgressResponseDTO getTodayMissionProgress(Long userId) {
        // 퀴즈 진행 상황
        int solvedQuizCount = quizMapper.countTodaySolvedQuiz(userId);

        // 뉴스 진행 상황
        int readNewsCount = newsMapper.countTodayNewsRead(userId);

        // 전체 달성률 계산 (가중치 적용)
        int quizScore = solvedQuizCount * QUIZ_SCORE_PER_ITEM;         // 퀴즈 1문제당 20%
        int newsScore = readNewsCount * NEWS_SCORE_PER_ITEM;           // 뉴스 1문제당 12%
        int totalProgress = quizScore + newsScore;
        totalProgress = Math.min(totalProgress, MAX_PROGRESS); // 100% 넘지 않게

        return MissionProgressResponseDTO.builder()
                .totalProgress(totalProgress)
                .quizCount(solvedQuizCount)
                .newsCount(readNewsCount)
                .QuizCompleted(solvedQuizCount >= QUIZ_GOAL)
                .NewsCompleted(readNewsCount >= NEWS_GOAL)
                .build();
    }
}