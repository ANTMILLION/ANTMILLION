package com.antmillion.auth.mapper;

import org.apache.ibatis.annotations.Param;

import com.antmillion.user.dto.MemberDTO;

public interface MemberMapper {

    int insertMember(MemberDTO dto); // userId를 useGeneratedKeys로 받아올 예정

    int countByEmail(@Param("email") String email);

    int countByNickname(@Param("nickname") String nickname);

    MemberDTO selectByEmail(@Param("email") String email);

    // 포인트 지급
    void updateUserPoint(@Param("userId") Long userId, @Param("point") Integer point);

    // 사용자 포인트 조회
    int selectUserPoint(@Param("userId") Long userId);

    // 사용자 닉네임 조회
    String selectUserNickName(@Param("userId") Long userId);
}