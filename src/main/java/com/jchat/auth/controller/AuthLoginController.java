package com.jchat.auth.controller;

import com.jchat.auth.dto.AuthLoginReqDto;
import com.jchat.auth.dto.AuthLoginResDto;
import com.jchat.auth.service.AuthLoginService;
import com.jchat.common.annotation.NoAuth;
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

    @NoAuth
    @PostMapping("/login")
    public ResponseEntity<AuthLoginResDto> AuthLogin(@RequestBody @Valid AuthLoginReqDto authLoginReqDto) {

        AuthLoginResDto resDto = new AuthLoginResDto();

        try {
            resDto = authLoginService.AuthLogin(authLoginReqDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(resDto);
        }

        return ResponseEntity.ok(resDto);
    }
}
