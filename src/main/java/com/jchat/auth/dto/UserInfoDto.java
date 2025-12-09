package com.jchat.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoDto {
    private String id;
    private String name;
    private Integer birth;
    private String email;
}
