package com.antmillion.kis.service;

import com.antmillion.kis.dto.KisAccessTokenResponse;
import com.antmillion.kis.repository.KisAccessTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class KisService {

    private final KisAccessTokenRedisRepository kisAccessTokenRedisRepository;
    private final KisApiService kisApiService;

    /**
     * KIS 액세스 토큰 조회/발급
     * Redis에 유효한 토큰이 있으면 반환, 없으면 신규 발급 후 Redis에 저장
     */
    public String getKisAccessToken() {
        // Redis에서 토큰 조회
        Optional<String> saved = kisAccessTokenRedisRepository.findAccessToken();
        if (saved.isPresent()) {
            return saved.get();
        }

        // 신규 발급
        KisAccessTokenResponse response = kisApiService.issueAccessToken();
        
        // Redis 저장 (TTL 자동 설정)
        kisAccessTokenRedisRepository.save(
            response.getAccess_token(), 
            response.getExpires_in()
        );

        return response.getAccess_token();
    }
}
