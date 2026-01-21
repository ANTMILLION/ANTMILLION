package com.antmillion.history.service;

import java.util.List;


import org.springframework.stereotype.Service;

import com.antmillion.history.dto.HistoryDTO;
import com.antmillion.history.mapper.HistoryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

	private final HistoryMapper historyMapper;

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
    public List<HistoryDTO> getHistoryListByDate(Long userId, String startDate, String endDate) {
        System.out.println("HistoryService: 날짜 기준 조회 - userId=" + userId + ", startDate=" + startDate + ", endDate=" + endDate);
        
        List<HistoryDTO> list = historyMapper.selectHistoryListByDate(userId, startDate, endDate);
        
        if (list != null) {
            System.out.println("조회된 경고 건수: " + list.size());
        }
        
        return list;
    }
    
    @Override
    public int getUnreadCount(Long userId) {
        System.out.println("HistoryService: 읽지 않은 경고 개수 조회 - userId=" + userId);
        int count = historyMapper.countUnreadAlerts(userId);
        System.out.println("읽지 않은 경고: " + count + "건");
        return count;
    }
    
    @Override
    public void markAsRead(Long historyId) {
        System.out.println("HistoryService: 경고 읽음 처리 - historyId=" + historyId);
        historyMapper.markAsRead(historyId);
    }
    
    @Override
    public void markAllAsRead(Long userId) {
        System.out.println("HistoryService: 모든 경고 읽음 처리 - userId=" + userId);
        historyMapper.markAllAsRead(userId);
    }
}