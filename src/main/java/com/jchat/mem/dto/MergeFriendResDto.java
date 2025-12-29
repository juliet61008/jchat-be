package com.jchat.mem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 친구 관계 수정 응답 DTO
 */

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MergeFriendResDto {
    private String succYn; // 성공여부
    private Long userNo; // 유저번호
    private Long relationUserNo; // 유저관계번호
}
