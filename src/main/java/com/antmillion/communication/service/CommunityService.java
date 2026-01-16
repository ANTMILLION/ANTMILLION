package com.antmillion.communication.service;

import java.util.List;
import com.antmillion.communication.dto.CommunityDTO;
import com.antmillion.communication.dto.CommunityDetailResponseDTO;

public interface CommunityService {
    
    /**
     * 특정 종목의 커뮤니티 글 목록 조회
     * @param stockCode 종목 코드
     * @return 커뮤니티 글 목록
     */
    List<CommunityDetailResponseDTO> getCommunityListByStock(String stockCode);
    
    /**
     * 커뮤니티 글 작성
     * @param communityDTO 커뮤니티 정보
     * @return 작성 성공 여부
     */
    boolean createCommunity(CommunityDTO communityDTO);
}