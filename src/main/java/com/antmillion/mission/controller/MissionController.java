package com.antmillion.mission.controller;

import com.antmillion.mission.dto.QuizQuestionResponseDTO;
import com.antmillion.mission.dto.QuizSubmissionRequestDTO;
import com.antmillion.mission.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/mission")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @GetMapping
    public String missionPage() {
        return "mission/mission";
    }

    @GetMapping("/daily")
    @ResponseBody
    public List<QuizQuestionResponseDTO> getDailyQuizData() {
        return missionService.getDailyQuiz();
    }

    @PostMapping("/check")
    @ResponseBody
    public boolean checkAnswer(@RequestBody QuizSubmissionRequestDTO requestDTO) {
        return missionService.checkAndLogAnswer(requestDTO);
    }
}