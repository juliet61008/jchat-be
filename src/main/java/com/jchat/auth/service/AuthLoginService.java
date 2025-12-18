package com.jchat.auth.service;

import com.jchat.auth.dto.*;
import com.jchat.auth.mapper.AuthLoginMapper;
import com.jchat.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthLoginService {

    private final AuthLoginMapper authLoginMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 로그인 인증 개발중. 추후 jwt 발급 로직 구현 예정
     * @param {{@link AuthLoginReqDto}} reqDto 로그인 인증 요청 DTO
     * @return {{@link AuthLoginResDto}} AuthLoginResDto 로그인 인증 응답 DTO
     */
    public AuthLoginResDto AuthLogin(AuthLoginReqDto reqDto) {

        PasswordByIdResDto passwordByIdResDto = authLoginMapper.searchPasswordById(new PasswordByIdReqDto(reqDto.getId()));
        // 객체 null 체크
        if (passwordByIdResDto == null) {
            // 아이디 없음
            return AuthLoginResDto.builder().code(-2).build();
        }
        // 객체 정상
        else {
            // 아이디 조회 불가
            if (passwordByIdResDto.getPassword().isEmpty()) {
                // 아이디 없음
                return AuthLoginResDto.builder().code(-2).build();
            }
        }

        if (!this.isMatchPassword(reqDto.getPassword(), passwordByIdResDto.getPassword())) {
            return AuthLoginResDto.builder().code(-3).build();
        }

        // 유저정보 조회
        UserInfoDto userInfoDto = authLoginMapper.searchAuthLogin(reqDto);

        // 유저정보 조회 불가
        if (userInfoDto == null) {
            return AuthLoginResDto.builder().code(-1).build();
        }

        String accessToken = jwtUtil.generateAccessToken(userInfoDto);
        String refreshToken = jwtUtil.generateRefreshToken(userInfoDto.getUserNo());

        return AuthLoginResDto.builder()
                .code(0)
                .userInfoDto(userInfoDto) // 유저정보
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 패스워드 비교
     * 유저가 입력한 평문과 db에 저장된 암호문 비교
     * @param {{@link AuthLoginReqDto}} reqDto
     * @return {boolean}
     */
    private boolean isMatchPassword(String password, String dbPassword) {

        if (password.isEmpty() || dbPassword.isEmpty()) return false;

        return passwordEncoder.matches(password, dbPassword);
    }
}
