package com.antmillion.mission.service;

import com.antmillion.user.dto.UserRankResponseDTO;
import java.util.Map;

public interface MissionService {
    UserRankResponseDTO getUserRankInfo(Long userId);
    Long getCurrentUserId();
    boolean isTodayMissionCompleted(Long userId);
    Map<String, Object> getTodayMissionStatus(Long userId);
}