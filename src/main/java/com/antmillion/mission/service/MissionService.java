package com.antmillion.mission.service;

import com.antmillion.mission.dto.MissionProgressResponseDTO;
import com.antmillion.user.dto.UserRankResponseDTO;

public interface MissionService {
    UserRankResponseDTO getUserRankInfo(Long userId);
    boolean isTodayMissionCompleted(Long userId);
    MissionProgressResponseDTO getTodayMissionStatus(Long userId);
}