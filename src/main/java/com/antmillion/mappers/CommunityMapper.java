package com.antmillion.mappers;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.antmillion.communication.dto.CommunityDTO;
import com.antmillion.communication.dto.CommunityDetailResponseDTO;

public interface CommunityMapper {
    
    /**
     * 특정 종목의 커뮤니티 글 목록 조회 (최신순, 닉네임 포함)
     * @param stockCode 종목 코드
     * @return 커뮤니티 글 목록
     */
    List<CommunityDetailResponseDTO> selectCommunityListByStock(@Param("stockCode") String stockCode);
    
    /**
     * 커뮤니티 글 등록
     * @param communityDTO 커뮤니티 정보
     * @return 등록된 행 수
     */
    int insertCommunity(CommunityDTO communityDTO);
}