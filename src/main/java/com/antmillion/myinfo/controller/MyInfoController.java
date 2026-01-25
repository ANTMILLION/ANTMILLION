package com.antmillion.myinfo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/myinfo")
@Controller
public class MyInfoController {

    @GetMapping
    public String MyInfoPage() {
        return "myinfo/myinfo";
    }
}
