package com.jchat.mem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 친구추가 요청 DTO
 */

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsertFriendReqDto {
    private Long userNo; // 유저번호
    private Long relationUserNo; // 유저관계번호
}
