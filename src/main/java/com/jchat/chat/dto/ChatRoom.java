package com.jchat.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 채팅방 기본 정보 DTO
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatRoom {
    private Long roomId;
    private String roomName;
    private String delYn;
    private String roomCd;
}
