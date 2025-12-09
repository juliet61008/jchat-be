package com.jchat.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 로그인 응답 DTO
 */

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginResDto extends TokenDto {
    @Builder.Default
    private int code = -1;
    private UserInfoDto userInfoDto;
}