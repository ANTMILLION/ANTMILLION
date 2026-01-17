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

    /**
     * 한국투자증권 api의 응답을 확인하기위한 컨트롤러
     * 실제로 사용할 때는 KisService만 사욯하면 될듯
     *
     */

    @GetMapping("/auth")
    public String auth() {
        return kisApiService.getKisAccessToken();
    }



}
