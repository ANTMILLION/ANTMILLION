package com.antmillion.auth.mapper;

import org.apache.ibatis.annotations.Param;

import com.antmillion.user.dto.MemberDTO;

public interface MemberMapper {

    int insertMember(MemberDTO dto); // userId를 useGeneratedKeys로 받아올 예정

    int countByEmail(@Param("email") String email);

    int countByNickname(@Param("nickname") String nickname);

    MemberDTO selectByEmail(@Param("email") String email);
}