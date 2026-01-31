package com.antmillion.quiz.service;

import com.antmillion.quiz.dto.QuizQuestionResponseDTO;
import com.antmillion.quiz.dto.QuizSubmissionRequestDTO;
import com.antmillion.quiz.dto.QuizSubmissionResponseDTO;

import java.util.List;

public interface QuizService {
    Long getCurrentUserId();
    List<QuizQuestionResponseDTO> getDailyQuiz();
    QuizSubmissionResponseDTO checkAndLogAnswer(QuizSubmissionRequestDTO requestDTO);
}