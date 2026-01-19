package com.antmillion.history.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.antmillion.history.dto.HistoryDTO;
import com.antmillion.history.mapper.HistoryMapper;

/**
 * @Service 어노테이션이 있어야 HistoryController에서 
 * @Autowired로 주입(Dependency Injection)받을 수 있습니다.
 */
@Service
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    private HistoryMapper historyMapper;

    @Override
    public List<HistoryDTO> getHistoryList(Long userId) {
        // 로그를 추가하여 데이터가 실제로 넘어오는지 콘솔에서 확인할 수 있습니다.
        System.out.println("HistoryService: " + userId + "번 사용자의 심리경고 조회 시작");
        
        List<HistoryDTO> list = historyMapper.selectHistoryList(userId);
        
        if (list != null) {
            System.out.println("조회된 경고 건수: " + list.size());
        }
        
        return list;
    }
}