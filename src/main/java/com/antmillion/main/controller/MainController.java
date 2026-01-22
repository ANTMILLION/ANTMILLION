package com.antmillion.main.controller;

import com.antmillion.mission.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping({"/", "/antmillion", "/antmillion/"})
@Controller
@RequiredArgsConstructor
public class MainController {

    private final MissionService missionService;

    @GetMapping
    public String mainPage(Model model) {
        Long userId =  missionService.getCurrentUserId();
        boolean missionCompleted = false;
        if (userId != null) {
            missionCompleted = missionService.isTodayMissionCompleted(userId);
        }
        model.addAttribute("missionCompleted", missionCompleted);
        return "main/main";
    }
}
