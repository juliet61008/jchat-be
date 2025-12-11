package com.jchat.mem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 유저 리스트 조회 REQ DTO
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchUserListReqDto {
    // 유저 번호
    private Long userNo;
}
