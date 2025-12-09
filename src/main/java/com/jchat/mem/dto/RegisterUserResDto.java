package com.jchat.mem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원가입 응답
 */

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserResDto {
    private String id; // 아이디
    private String succYn; // 성공여부
}
