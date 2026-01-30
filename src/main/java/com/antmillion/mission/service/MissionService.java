package com.antmillion.mission.service;

import com.antmillion.mission.dto.QuizQuestionResponseDTO;
import com.antmillion.mission.dto.QuizSubmissionRequestDTO;
import com.antmillion.mission.dto.QuizSubmissionResponseDTO;
import com.antmillion.user.dto.UserRankResponseDTO;

import java.util.List;
import java.util.Map;

public interface MissionService {
    List<QuizQuestionResponseDTO> getDailyQuiz();
    QuizSubmissionResponseDTO checkAndLogAnswer(QuizSubmissionRequestDTO requestDTO);
    UserRankResponseDTO getUserMissionStatus(Long userId);
    Long getCurrentUserId();
    boolean isTodayMissionCompleted(Long userId);
    Map<String, Object> getTodayMissionStatus(Long userId);
}