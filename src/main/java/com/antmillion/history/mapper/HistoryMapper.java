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
    
    HistoryDTO getLastAlert(
            @Param("userId") Long userId,
            @Param("stockCode") String stockCode,
            @Param("biasType") String biasType
        );

        int countUnreadAlerts(@Param("userId") Long userId);

        int markAsRead(@Param("historyId") Long historyId);
    }

