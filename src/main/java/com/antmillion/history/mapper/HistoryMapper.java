package com.antmillion.history.mapper;

import com.antmillion.history.dto.HistoryDTO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HistoryMapper {

	List<HistoryDTO> selectHistoryList(Long userId);

    List<HistoryDTO> selectHistoryListByDate(
        @Param("userId") Long userId,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate
    );

    int insertHistory(HistoryDTO history);
    int countUnreadAlerts(@Param("userId") Long userId);    
    int markAsRead(@Param("historyId") Long historyId);    
    int markAllAsRead(@Param("userId") Long userId);
    
    HistoryDTO getLastAlert(
        @Param("userId") Long userId,
        @Param("stockCode") String stockCode,
        @Param("biasType") String biasType
    );
}
    
