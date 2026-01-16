package com.antmillion.kis.service;

import com.antmillion.kis.dto.KisAccessToken;
import com.antmillion.kis.dto.KisAccessTokenResponse;
import com.antmillion.kis.repository.KisAccessTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class KisService {

    private final KisAccessTokenMapper  kisAccessTokenMapper;
    private final KisApiService kisApiService;

    public String getKisAccessToken() {
        Optional<KisAccessToken> saved = kisAccessTokenMapper.findLatestAccessToken();
        if (saved.isPresent() && saved.get().getExpiresAt().isAfter(LocalDateTime.now())) { // 서버 위치 기준 시간이라 나중에 변경해야할 수도 있음
            return saved.get().getToken();
        }

        //신규 발급
        KisAccessTokenResponse response = kisApiService.issueAccessToken();
        //DB 저장
        KisAccessToken accessToken = new KisAccessToken();
        accessToken.setToken(response.getAccess_token());
        accessToken.setExpiresAt(LocalDateTime.now().plusSeconds(response.getExpires_in()));
        kisAccessTokenMapper.save(accessToken);

        return response.getAccess_token();
    }
}
