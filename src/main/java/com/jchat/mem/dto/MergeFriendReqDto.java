package com.jchat.mem.dto;

import jakarta.validation.constraints.NotBlank;
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
public class MergeFriendReqDto {
    private Long userNo; // 유저번호
    @NotBlank
    private Long relationUserNo; // 유저관계번호
    private String likeYn; // 즐겨찾기여부
    private String blockYn; // 차단여부
    private String aliasNm; // 별칭
}
