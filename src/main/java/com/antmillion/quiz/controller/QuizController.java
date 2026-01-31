package com.antmillion.quiz.controller;

import com.antmillion.quiz.dto.QuizQuestionResponseDTO;
import com.antmillion.quiz.dto.QuizSubmissionRequestDTO;
import com.antmillion.quiz.dto.QuizSubmissionResponseDTO;
import com.antmillion.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
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
        try {
            return quizService.getDailyQuiz();
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("퀴즈 조회 중 오류: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @PostMapping("/api/mission/quiz/check")
    @ResponseBody
    public QuizSubmissionResponseDTO checkAnswer(@RequestBody QuizSubmissionRequestDTO requestDTO) {
        try {
            return quizService.checkAndLogAnswer(requestDTO);
        } catch (Exception e) {
            System.err.println("퀴즈 채점 중 오류: " + e.getMessage());
            e.printStackTrace();
            return QuizSubmissionResponseDTO.builder()
                    .isCorrect(false)
                    .point(0)
                    .message("오류가 발생했습니다. 다시 시도해주세요.")
                    .build();
        }
    }
}
