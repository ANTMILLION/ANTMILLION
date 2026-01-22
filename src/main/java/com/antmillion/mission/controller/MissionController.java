package com.antmillion.mission.controller;

import com.antmillion.mission.dto.QuizQuestionResponseDTO;
import com.antmillion.mission.dto.QuizSubmissionRequestDTO;
import com.antmillion.mission.service.MissionService;
import com.antmillion.user.dto.UserRankResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public boolean checkAnswer(@RequestBody QuizSubmissionRequestDTO requestDTO) {
        try {
            return missionService.checkAndLogAnswer(requestDTO);
        } catch (Exception e) {
            System.err.println("퀴즈 채점 중 오류: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/status")
    @ResponseBody
    public UserRankResponseDTO getMissionStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new IllegalStateException("로그인이 필요한 서비스입니다.");
        }

        try {
            Long userId = Long.valueOf(auth.getPrincipal().toString());
            return missionService.getUserMissionStatus(userId);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("유효하지 않은 사용자 정보입니다.");
        }
    }
}