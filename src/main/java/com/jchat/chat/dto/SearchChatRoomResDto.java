package com.jchat.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 채팅방 조회 REQ DTO
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchChatRoomResDto {
    // 채팅방번호
    private Long roomId;

    // 채팅방 유저 리스트
    private List<ChatRoomUser> chatRoomUserList;

    // 채팅방 메세지 내용 리스트
    private List<ChatRoomMsg> chatRoomMsgList;
}
