package com.antmillion.auth.mapper;

import org.apache.ibatis.annotations.Param;

public interface SocialMapper {
    Long selectUserIdByKakaoId(@Param("kakaoId") long kakaoId);
    int insertSocial(@Param("kakaoId") long kakaoId, @Param("userId") long userId);
}
