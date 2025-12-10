package com.jchat.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 채팅방 insert DTO
 */

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class InsertChatRoomReqDto {
    @JsonIgnore
    private Long roomId;
    private String roomName;
}
