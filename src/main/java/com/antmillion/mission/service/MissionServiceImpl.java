package com.antmillion.mission.service;

import com.antmillion.mission.dto.MissionStatusResponseDTO;
import com.antmillion.quiz.mapper.QuizMapper;
import com.antmillion.news.mapper.NewsMapper;
import com.antmillion.user.dto.UserRankResponseDTO;
import com.antmillion.user.service.AntRankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MissionServiceImpl implements MissionService {
    private final QuizMapper quizMapper;
    private final AntRankService antRankService;
    private final NewsMapper newsMapper;

    @Override
    public UserRankResponseDTO getUserRankInfo(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID가 필요합니다.");
        }

        try {
            return antRankService.getUserRankInfo(userId);
        } catch (Exception e) {
            System.err.println("미션 상태 조회 중 오류: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("미션 상태 조회 중 오류가 발생했습니다.", e);
        }
    }

    public boolean isTodayMissionCompleted(Long userId) {
        MissionStatusResponseDTO status = getTodayMissionStatus(userId);
        // totalProgress가 100이면 완료로 간주
        return status.getTotalProgress() >= 100;
    }

    public MissionStatusResponseDTO getTodayMissionStatus(Long userId) {
        if (userId == null) {
            return MissionStatusResponseDTO.builder()
                    .totalProgress(0)
                    .quizCount(0)
                    .newsCount(0)
                    .isQuizCompleted(false)
                    .isNewsCompleted(false)
                    .build();
        }

        // 퀴즈 진행 상황
        int solvedQuizCount = quizMapper.countTodaySolvedQuiz(userId);
        int quizGoal = 2;

        // 뉴스 진행 상황
        int readNewsCount = newsMapper.countTodayNewsRead(userId);
        int newsGoal = 5;

        // 전체 달성률 계산 (가중치 적용)
        int quizScore = solvedQuizCount * 20;         // 퀴즈 1문제당 20%
        int newsScore = readNewsCount * 12;           // 뉴스 1문제당 12%
        int totalProgress = quizScore + newsScore;
        totalProgress = Math.min(totalProgress, 100); // 100% 넘지 않게

        return MissionStatusResponseDTO.builder()
                .totalProgress(totalProgress)
                .quizCount(solvedQuizCount)
                .newsCount(readNewsCount)
                .isQuizCompleted(solvedQuizCount >= quizGoal)
                .isNewsCompleted(readNewsCount >= newsGoal)
                .build();
    }
}