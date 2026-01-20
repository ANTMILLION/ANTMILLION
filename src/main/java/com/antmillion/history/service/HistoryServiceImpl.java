package com.antmillion.history.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.antmillion.history.dto.HistoryDTO;
import com.antmillion.history.mapper.HistoryMapper;

@Service
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    private HistoryMapper historyMapper;

    @Override
    public List<HistoryDTO> getHistoryList(Long userId) {
        System.out.println("HistoryService: " + userId + "번 사용자의 심리경고 조회 시작");

        List<HistoryDTO> list = historyMapper.selectHistoryList(userId);

        if (list != null) {
            System.out.println("조회된 경고 건수: " + list.size());
        }

        return list;
    }

    @Override
    public List<HistoryDTO> getHistoryListByDate(
            Long userId,
            String startDate,
            String endDate
    ) {
        System.out.println("HistoryService: 날짜 조건 조회");
        System.out.println("userId=" + userId + ", startDate=" + startDate + ", endDate=" + endDate);

        return historyMapper.selectHistoryListByDate(userId, startDate, endDate);
    }
}
