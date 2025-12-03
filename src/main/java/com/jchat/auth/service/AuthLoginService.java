package com.jchat.auth.service;

import com.jchat.auth.dto.AuthLoginReqDto;
import com.jchat.auth.dto.AuthLoginResDto;
import com.jchat.auth.dto.UserInfoDto;
import com.jchat.auth.mapper.AuthLoginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthLoginService {

    private final AuthLoginMapper authLoginMapper;

    /**
     * 로그인 인증 개발중. 추후 jwt 발급 로직 구현 예정
     * @param authLoginReqDto 로그인 인증 요청 DTO
     * @return AuthLoginResDto 로그인 인증 응답 DTO
     */
    public AuthLoginResDto AuthLogin(AuthLoginReqDto authLoginReqDto) {

        UserInfoDto userInfoDto = authLoginMapper.searchAuthLogin(authLoginReqDto);

        // 유저정보 조회 불가
        if (userInfoDto == null) {
            return AuthLoginResDto.builder().code(-1).build();
        }

        return AuthLoginResDto.builder()
                .code(0)
                .id(userInfoDto.getId()) // 아이디
                .accessToken("")
                .refreshToken("")
                .build();
    }
}
