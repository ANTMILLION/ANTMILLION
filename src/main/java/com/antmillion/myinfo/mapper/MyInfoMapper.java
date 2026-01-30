package com.antmillion.myinfo.mapper;

import org.apache.ibatis.annotations.Param;

import com.antmillion.myinfo.dto.MyAuthInfoDTO;
import com.antmillion.myinfo.dto.MyInfoDTO;

public interface MyInfoMapper {

    MyInfoDTO selectMyInfo(@Param("userId") long userId);

    MyAuthInfoDTO selectAuthInfo(@Param("userId") long userId);

    int updatePassword(@Param("userId") long userId, @Param("password") String encodedPassword);

    int deleteMember(@Param("userId") long userId);
}
