package com.antmillion.mission.service;

import com.antmillion.quiz.mapper.QuizMapper;
import com.antmillion.news.mapper.NewsMapper;
import com.antmillion.user.dto.UserRankResponseDTO;
import com.antmillion.user.service.AntRankService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MissionServiceImpl implements MissionService {
    private final QuizMapper quizMapper;
    private final AntRankService antRankService;
    private final NewsMapper newsMapper;

    // 로그인한 유저 ID를 가져오는 메서드
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return Long.valueOf(auth.getPrincipal().toString());
        }
        return null;
    }

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
        Map<String, Object> status = getTodayMissionStatus(userId);
        // totalProgress가 100이면 완료로 간주
        int progress = (int)status.get("totalProgress");
        return progress >= 100;
    }

    public Map<String, Object> getTodayMissionStatus(Long userId) {
        Map<String, Object> status = new HashMap<>();

        if (userId == null) {
            status.put("totalProgress", 0);
            return status;
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

        // 데이터 담기
        status.put("totalProgress", totalProgress);
        status.put("quizCount", solvedQuizCount);
        status.put("newsCount", readNewsCount);
        status.put("isQuizCompleted", solvedQuizCount >= quizGoal);
        status.put("isNewsCompleted", readNewsCount >= newsGoal);
        return status;
    }
}