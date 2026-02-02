package com.antmillion.mission.controller;

import com.antmillion.mission.dto.MissionStatusResponseDTO;
import com.antmillion.mission.service.MissionService;
import com.antmillion.user.dto.UserRankResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @GetMapping("/mission")
    public String missionPage() {
        return "mission/mission";
    }

    @GetMapping("/api/mission/user-rank")
    @ResponseBody
    public UserRankResponseDTO getUserRankInfo() {
        Long userId = currentUserId();
        if (userId == null) {
            return UserRankResponseDTO.builder()
                    .rankName("게스트")
                    .rankImage("/images/defaultant.png")
                    .currentPoint(0)
                    .nextRankPoint(0)
                    .neededPoint(0)
                    .currentRankStartPoint(0)
                    .nickName("게스트")
                    .currentRankId(0)
                    .build();
        }
        return missionService.getUserRankInfo(userId);
    }

    @GetMapping("/api/mission/today-progress")
    @ResponseBody
    public MissionStatusResponseDTO getTodayMissionStatus() {
        Long userId = currentUserId();
        if (userId == null) {
            return MissionStatusResponseDTO.builder()
                    .totalProgress(0)
                    .quizCount(0)
                    .newsCount(0)
                    .QuizCompleted(false)
                    .NewsCompleted(false)
                    .build();
        }
        return missionService.getTodayMissionStatus(userId);
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