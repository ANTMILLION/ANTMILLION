package com.antmillion.mission.service;

import com.antmillion.mission.dto.QuizQuestionResponseDTO;
import com.antmillion.mission.dto.QuizSubmissionRequestDTO;
import com.antmillion.user.dto.UserRankResponseDTO;

import java.util.List;

public interface MissionService {
    List<QuizQuestionResponseDTO> getDailyQuiz();
    boolean checkAndLogAnswer(QuizSubmissionRequestDTO requestDTO);
    UserRankResponseDTO getUserMissionStatus(Long userId);
}