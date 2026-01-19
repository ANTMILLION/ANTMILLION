package com.antmillion.mission.service;

import com.antmillion.mission.dto.QuizQuestionResponseDTO;
import java.util.List;

public interface MissionService {
    List<QuizQuestionResponseDTO> getDailyQuiz();
}