package com.antmillion.quiz.controller;

import com.antmillion.news.dto.NewsProgressResponseDTO;
import com.antmillion.quiz.dto.QuizProgressResponseDTO;
import com.antmillion.quiz.dto.QuizQuestionResponseDTO;
import com.antmillion.quiz.dto.QuizSubmissionRequestDTO;
import com.antmillion.quiz.dto.QuizSubmissionResponseDTO;
import com.antmillion.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/quiz")
    public String quizPage() {
        return "quiz/quiz";
    }

    @GetMapping("/api/mission/quiz/daily")
    @ResponseBody
    public List<QuizQuestionResponseDTO> getDailyQuizData() {
        Long userId = currentUserId();
        if (userId == null) {
            return new ArrayList<>();
        }
        return quizService.getDailyQuiz(userId);
    }

    @PostMapping("/api/mission/quiz/check")
    @ResponseBody
    public QuizSubmissionResponseDTO checkAnswer(@RequestBody QuizSubmissionRequestDTO requestDTO) {
        Long userId = currentUserId();
        if (userId == null) {
            return QuizSubmissionResponseDTO.builder()
                    .isCorrect(false)
                    .point(0)
                    .message("로그인이 필요합니다.")
                    .build();
        }
        return quizService.checkAndLogAnswer(userId, requestDTO);
    }

    // 현재 달성률 조회
    @GetMapping("/api/mission/quiz/progress")
    @ResponseBody
    public QuizProgressResponseDTO getQuizProgress() {
        Long userId = currentUserId();

        if (userId == null) {
            return QuizProgressResponseDTO.builder()
                    .solvedCount(0)
                    .totalCount(2)
                    .progress(0)
                    .build();
        }
        return quizService.getQuizProgress(userId);
    }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        try {
            return Long.valueOf(auth.getPrincipal().toString());
        } catch (Exception e) {
            return null;
        }
    }
}
