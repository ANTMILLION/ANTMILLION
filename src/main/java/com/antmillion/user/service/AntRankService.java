package com.antmillion.user.service;

import com.antmillion.user.dto.UserRankResponseDTO;

public interface AntRankService {
    UserRankResponseDTO calculateRankStatus(int currentPoint);
}
