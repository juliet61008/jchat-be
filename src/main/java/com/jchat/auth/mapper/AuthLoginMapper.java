package com.jchat.auth.mapper;

import com.jchat.auth.dto.AuthLoginReqDto;
import com.jchat.auth.dto.UserInfoDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthLoginMapper {
    UserInfoDto searchAuthLogin(AuthLoginReqDto authLoginReqDto);
}