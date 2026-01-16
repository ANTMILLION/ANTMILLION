package com.antmillion.communication.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.antmillion.communication.dto.CommunityDTO;
import com.antmillion.communication.dto.CommunityDetailResponseDTO;
import com.antmillion.communication.service.CommunityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {
    
    private final CommunityService communityService;
    
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCommunityList(
            @RequestParam("stockCode") String stockCode) {
        log.info("커뮤니티 목록 조회 요청 - stockCode: {}", stockCode);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<CommunityDetailResponseDTO> communityList = 
                    communityService.getCommunityListByStock(stockCode);
            
            response.put("success", true);
            response.put("data", communityList);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("커뮤니티 목록 조회 실패", e);
            response.put("success", false);
            response.put("message", "목록 조회에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/write")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createCommunity(
            @RequestBody CommunityDTO communityDTO) {
        log.info("커뮤니티 글 작성 요청 - {}", communityDTO);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean result = communityService.createCommunity(communityDTO);
            
            if (result) {
                response.put("success", true);
                response.put("message", "작성 완료");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "작성 실패");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            log.error("커뮤니티 글 작성 실패", e);
            response.put("success", false);
            response.put("message", "작성에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}