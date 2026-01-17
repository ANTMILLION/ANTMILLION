package com.antmillion.kis.controller;

import com.antmillion.kis.service.KisApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/kis")
public class KisController {

    private final KisApiService kisApiService;

    @GetMapping("/auth")
    public String auth() {
        return kisApiService.getKisAccessToken();
    }

}
