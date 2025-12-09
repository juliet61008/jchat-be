package com.jchat.mem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원가입 요청 DTO
 */

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserReqDto {
    private String id; // 아이디
    private String password; // 비밀번호
    private String name; // 이름
    private Integer birth; // 생년월일
    private String emailId; // 이메일아이디
    private String emailDomain; // 이메일주소
}
