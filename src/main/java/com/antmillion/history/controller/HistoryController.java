package com.antmillion.history.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.antmillion.history.service.HistoryService;
import com.antmillion.history.dto.HistoryDTO;

import java.util.List;

@RestController
@RequestMapping("/history/api")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @GetMapping("/list")
    public List<HistoryDTO> getHistoryList(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        Long userId = 1L;

        // 날짜가 있으면 날짜 기준 조회
        if (startDate != null && endDate != null) {
            return historyService.getHistoryListByDate(userId, startDate, endDate);
        }

        // 날짜 없으면 기존 전체 조회
        return historyService.getHistoryList(userId);
    }
}
