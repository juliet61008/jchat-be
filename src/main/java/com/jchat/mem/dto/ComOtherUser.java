package com.jchat.mem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ComOtherUser {
    private Long userNo; // 타유저 회원번호
    private String id; // 타유저 아이디
    private String name; // 타유저 이름
    private String aliasNm; // 타유저 별칭
    private Integer birth; // 타유저 생일
    private String friendYn; // 타유저 친구여부
    private String likeYn; // 타유저 즐겨찾기 여부
    private String blockYn; // 타유저 친구차단 여부
}
