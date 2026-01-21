package com.antmillion.history.mapper;

import com.antmillion.history.dto.HistoryDTO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HistoryMapper {

    List<HistoryDTO> selectHistoryList(
        @Param("userId") Long userId
    );

    List<HistoryDTO> selectHistoryListByDate(
        @Param("userId") Long userId,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate
    );

    int insertHistory(HistoryDTO history);
}
