package com.antmillion.history.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController; // 중요!
import com.antmillion.history.service.HistoryService;
import com.antmillion.history.dto.HistoryDTO;
import java.util.List;

@RestController // 데이터를 전송하는 API 컨트롤러로 설정
@RequestMapping("/history/api")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @GetMapping("/list")
    public List<HistoryDTO> getHistoryList() {
        Long userId = 1L; // 실제 세션에서 가져올 ID
        // DB에서 데이터를 가져와 JSON 형태로 반환합니다.
        return historyService.getHistoryList(userId);
    }
}