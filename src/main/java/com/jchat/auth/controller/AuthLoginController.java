package com.jchat.auth.controller;

import com.jchat.auth.dto.AuthLoginReqDto;
import com.jchat.auth.dto.AuthLoginResDto;
import com.jchat.auth.dto.TokenDto;
import com.jchat.auth.dto.UserInfoDto;
import com.jchat.auth.service.AuthLoginService;
import com.jchat.common.advice.CustomException;
import com.jchat.common.annotation.NoAuth;
import com.jchat.common.context.UserContext;
import com.jchat.common.dto.ApiResponse;
import com.jchat.common.util.CookieUtil;
import com.jchat.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.el.parser.Token;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthLoginController {

    private final AuthLoginService authLoginService;
    private final JwtUtil jwtUtil;

    /**
     * 로그인
     * @param authLoginReqDto
     * @param response
     * @return
     */
    @NoAuth
    @PostMapping("/login")
    public AuthLoginResDto AuthLogin(@RequestBody @Valid AuthLoginReqDto authLoginReqDto, HttpServletResponse response) {

        AuthLoginResDto resDto = new AuthLoginResDto();

        resDto = authLoginService.AuthLogin(authLoginReqDto);

        // 쿠키에 토큰 추가
        jwtUtil.addAccessTokenCookie(response, resDto.getAccessToken());
        jwtUtil.addRefreshTokenCookie(response, resDto.getRefreshToken());

        return resDto;
    }

    @NoAuth
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response, HttpServletRequest request) {
        // 쿠키 삭제
        jwtUtil.deleteTokenCookies(response);
        return ResponseEntity.ok("로그아웃 성공");
    }

    @NoAuth
    @PostMapping("/refreshToken")
    public TokenDto refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // 1. refreshToken 추출
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CustomException(401, "No refresh token");
        }

        String refreshToken = authHeader.substring(7);

        if (refreshToken.isEmpty()) {
            throw new CustomException(401, "No refresh token");
        }

        // 2. refreshToken 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            // refreshToken도 만료 > 재로그인 필요
            throw new CustomException(401, "Refresh token expired");
        }

        // 3. 사용자 정보 추출
        UserInfoDto userInfo = jwtUtil.getUserInfoFromToken(refreshToken);

        // 4. 새 accessToken 발급
        String newAccessToken = jwtUtil.generateAccessToken(userInfo);
        jwtUtil.addAccessTokenCookie(response, newAccessToken);

        // 5. refreshToken도 갱신 (Refresh Token Rotation)
        // Refresh Token 생성 (userId만)
        String newRefreshToken = jwtUtil.generateRefreshToken(userInfo.getUserNo());
        jwtUtil.addRefreshTokenCookie(response, newRefreshToken);

        return TokenDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @NoAuth
    @GetMapping("/isLogin")
    public boolean igLogin() {
        return UserContext.hasUser();
    }
}
