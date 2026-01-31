package com.antmillion.mission.controller;

import com.antmillion.mission.service.MissionService;
import com.antmillion.user.dto.UserRankResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

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
        Long userId = missionService.getCurrentUserId();
        return missionService.getUserRankInfo(userId);
    }

    @GetMapping("/api/mission/today-progress")
    @ResponseBody
    public Map<String, Object> getTodayMissionStatus() {
        Long userId = missionService.getCurrentUserId();
        return missionService.getTodayMissionStatus(userId);
    }
}