package com.jchat.mem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 유저 리스트 조회 RES DTO
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchUserListResDto {
    // 유저 번호
    private Long userNo;
    // 유저 아이디
    private String id;
    // 유저 이름
    private String name;
    // 유저 별칭
    private String aliasNm;
}
