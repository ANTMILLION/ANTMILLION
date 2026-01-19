package com.antmillion.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.antmillion.auth.dto.MemberAuthDTO;


@Mapper
public interface MemberMapper {
  MemberAuthDTO selectByEmail(@Param("email") String email);
}