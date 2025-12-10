package com.jchat.auth.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 유저정보 DTO
 */

@Getter
@Builder
public class UserInfoDto {
    private Long userNo;
    private String id;
    private String name;
    private Integer birth;
    private String email;
}
