package com.antmillion.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.antmillion.auth.dto.AuthMemberDTO;


@Mapper
public interface AuthMemberMapper {
  AuthMemberDTO selectByEmail(@Param("email") String email);
}