package com.jchat.auth.mapper;

import com.jchat.auth.dto.AuthLoginReqDto;
import com.jchat.auth.dto.LoginInfoByIdReqDto;
import com.jchat.auth.dto.LoginInfoByIdResDto;
import com.jchat.auth.dto.UserInfoDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthLoginMapper {
    LoginInfoByIdResDto searchLoginInfoById(LoginInfoByIdReqDto loginInfoByIdReqDto);
    UserInfoDto searchUserInfoByUserNo(Long userNo);
}