package com.antmillion.mission.controller;

import com.antmillion.mission.dto.QuizQuestionResponseDTO;
import com.antmillion.mission.dto.QuizSubmissionRequestDTO;
import com.antmillion.mission.dto.QuizSubmissionResponseDTO;
import com.antmillion.mission.service.MissionService;
import com.antmillion.user.dto.UserRankResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/mission")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @GetMapping
    public String missionPage() {
        return "mission/mission";
    }

    @GetMapping("/quiz")
    public String quizPage() {
        return "mission/quiz";
    }

    @GetMapping("/daily")
    @ResponseBody
    public List<QuizQuestionResponseDTO> getDailyQuizData() {
        try {
            return missionService.getDailyQuiz();
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("퀴즈 조회 중 오류: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @PostMapping("/check")
    @ResponseBody
    public QuizSubmissionResponseDTO checkAnswer(@RequestBody QuizSubmissionRequestDTO requestDTO) {
        try {
            return missionService.checkAndLogAnswer(requestDTO);
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

    @GetMapping("/status")
    @ResponseBody
    public UserRankResponseDTO getMissionStatus() {
        Long userId = missionService.getCurrentUserId();
        return missionService.getUserMissionStatus(userId);
    }

    @GetMapping("/today-status")
    @ResponseBody
    public Map<String, Object> getTodayMissionStatus() {
        Long userId = missionService.getCurrentUserId();
        return missionService.getTodayMissionStatus(userId);
    }
}