package com.antmillion.history.service;

import com.antmillion.history.dto.HistoryDTO;
import java.util.List;

public interface HistoryService {

	List<HistoryDTO> getHistoryList(Long userId);
	List<HistoryDTO> getHistoryListByDate(Long userId, String startDate, String endDate);

	int getUnreadCount(Long userId);
	void markAsRead(Long historyId);
	void markAllAsRead(Long userId);
}
