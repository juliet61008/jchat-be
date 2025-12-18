package com.jchat.auth.controller;

import com.jchat.auth.dto.AuthLoginReqDto;
import com.jchat.auth.dto.AuthLoginResDto;
import com.jchat.auth.dto.UserInfoDto;
import com.jchat.auth.service.AuthLoginService;
import com.jchat.common.annotation.NoAuth;
import com.jchat.common.util.CookieUtil;
import com.jchat.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthLoginController {

    private final AuthLoginService authLoginService;
    private final JwtUtil jwtUtil;

    @NoAuth
    @PostMapping("/login")
    public ResponseEntity<AuthLoginResDto> AuthLogin(@RequestBody @Valid AuthLoginReqDto authLoginReqDto, HttpServletResponse response) {

        AuthLoginResDto resDto = new AuthLoginResDto();

        try {
            resDto = authLoginService.AuthLogin(authLoginReqDto);

            // 쿠키에 토큰 추가
            jwtUtil.addAccessTokenCookie(response, resDto.getAccessToken());
            jwtUtil.addRefreshTokenCookie(response, resDto.getRefreshToken());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(resDto);
        }

        return ResponseEntity.ok(resDto);
    }

    @NoAuth
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response, HttpServletRequest request) {
        System.out.println("현재 쿠키들: " + Arrays.toString(request.getCookies()));
        // 쿠키 삭제
        jwtUtil.deleteTokenCookies(response);
        return ResponseEntity.ok("로그아웃 성공");
    }

    @NoAuth
    @PostMapping("/refreshToken")
    public ResponseEntity<?> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // 1. refreshToken 추출
        String refreshToken = CookieUtil.extractTokenFromCookie(request, "refreshToken");

        if (refreshToken == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("code", 401, "message", "No refresh token"));
        }

        // 2. refreshToken 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            // refreshToken도 만료 → 재로그인 필요
            return ResponseEntity.status(401)
                    .body(Map.of("code", 401, "message", "Refresh token expired"));
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

        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "Token refreshed"
        ));
    }
}
