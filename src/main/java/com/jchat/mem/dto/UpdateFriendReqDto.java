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
public class UpdateFriendReqDto {
    private Long userNo; // 유저번호
    private Long relationUserNo; // 유저관계번호
    private String likeYn; // 즐겨찾기여부
    private String blockYn; // 차단여부
    private String aliasNm; // 별칭
}
