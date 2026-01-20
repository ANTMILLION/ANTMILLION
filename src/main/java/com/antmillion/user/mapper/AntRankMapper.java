package com.antmillion.user.mapper;

import com.antmillion.user.dto.AntRankDTO;
import java.util.List;

public interface AntRankMapper {
    List<AntRankDTO> selectAllRanks();
}