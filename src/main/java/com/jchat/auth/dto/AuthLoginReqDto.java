package com.jchat.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 로그인 요청 DTO
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthLoginReqDto {
    private String id;
    private String password;
}
