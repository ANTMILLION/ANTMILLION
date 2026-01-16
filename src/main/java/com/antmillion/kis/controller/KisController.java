package com.antmillion.kis.controller;

import com.antmillion.kis.service.KisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/kis")
public class KisController {

    private final KisService kisService;

    @GetMapping("/auth")
    public String auth() {
        return kisService.getKisAccessToken();
    }

}
