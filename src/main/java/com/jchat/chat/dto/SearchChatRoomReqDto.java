package com.jchat.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 채팅방 조회 REQ DTO
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchChatRoomReqDto {
    // 채팅방번호
    private Long roomId;

    // 회원번호
    private Long userNo;

}
