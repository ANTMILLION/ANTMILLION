package com.antmillion.kis.repository;

import com.antmillion.kis.dto.KisAccessToken;

import java.util.Optional;

public interface KisAccessTokenMapper {

    int save(KisAccessToken kisAccessToken);

    Optional<KisAccessToken> findLatestAccessToken();

}
