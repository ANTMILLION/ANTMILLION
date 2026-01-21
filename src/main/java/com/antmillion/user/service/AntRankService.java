package com.antmillion.user.service;

import com.antmillion.user.dto.UserRankResponseDTO;

public interface AntRankService {
    UserRankResponseDTO calculateRankStatus(Long userId, int currentPoint);
    UserRankResponseDTO getUserRankInfo(Long userId);
}
