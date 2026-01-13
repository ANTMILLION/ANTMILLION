package com.antmillion.mission;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mission")
public class MissionController {

    @GetMapping
    public String missionPage() {
        return "mission/mission";
    }
}
