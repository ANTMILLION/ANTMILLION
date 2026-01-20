package com.antmillion.mission.service;

import com.antmillion.mission.dto.QuizQuestionResponseDTO;
import com.antmillion.mission.dto.QuizSubmissionRequestDTO;
import java.util.List;

public interface MissionService {
    List<QuizQuestionResponseDTO> getDailyQuiz();
    boolean checkAndLogAnswer(QuizSubmissionRequestDTO requestDTO);
}