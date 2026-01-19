package com.antmillion.history.service;

import com.antmillion.history.dto.HistoryDTO;
import java.util.List;

public interface HistoryService {
    List<HistoryDTO> getHistoryList(Long userId);
}