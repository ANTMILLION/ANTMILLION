package com.antmillion.communication.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.antmillion.communication.dto.CommunityDTO;
import com.antmillion.communication.dto.CommunityDetailResponseDTO;
import com.antmillion.mappers.CommunityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


 //커뮤니티 Service 구현체

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommunityServiceImpl implements CommunityService {
    
    private final CommunityMapper communityMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CommunityDetailResponseDTO> getCommunityListByStock(String stockCode) {
        log.info("커뮤니티 목록 조회 - stockCode: {}", stockCode);
        return communityMapper.selectCommunityListByStock(stockCode);
    }

    @Override
    public boolean createCommunity(CommunityDTO communityDTO) {
        log.info("커뮤니티 글 작성 - {}", communityDTO);
        int result = communityMapper.insertCommunity(communityDTO);
        return result > 0;
    }
}