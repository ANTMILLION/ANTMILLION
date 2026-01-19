package com.antmillion.history.mapper;

import com.antmillion.history.dto.HistoryDTO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HistoryMapper {
    // 사용자의 심리 경고 내역 전체 조회
    List<HistoryDTO> selectHistoryList(Long userId);
}