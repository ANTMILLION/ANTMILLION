package com.antmillion.mission.service;

import com.antmillion.mission.dto.MissionStatusResponseDTO;
import com.antmillion.user.dto.UserRankResponseDTO;

public interface MissionService {
    UserRankResponseDTO getUserRankInfo(Long userId);
    boolean isTodayMissionCompleted(Long userId);
    MissionStatusResponseDTO getTodayMissionStatus(Long userId);
}