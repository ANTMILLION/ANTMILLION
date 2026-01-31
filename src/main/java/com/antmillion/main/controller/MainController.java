package com.antmillion.main.controller;

import com.antmillion.mission.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        Long userId =  currentUserId();
        boolean missionCompleted = false;
        if (userId != null) {
            missionCompleted = missionService.isTodayMissionCompleted(userId);
        }
        model.addAttribute("missionCompleted", missionCompleted);
        return "main/main";
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
