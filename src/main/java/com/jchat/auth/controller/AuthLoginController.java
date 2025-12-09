package com.jchat.auth.controller;

import com.jchat.auth.dto.AuthLoginReqDto;
import com.jchat.auth.dto.AuthLoginResDto;
import com.jchat.auth.service.AuthLoginService;
import com.jchat.common.annotation.NoAuth;
import com.jchat.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String> logout(HttpServletResponse response) {
        // 쿠키 삭제
        jwtUtil.deleteTokenCookies(response);
        return ResponseEntity.ok("로그아웃 성공");
    }

}
