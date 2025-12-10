package com.jchat.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 채팅방 유저 insert DTO
 */

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class InsertChatRoomUserReqDto {
    // 채팅방 번호
    @JsonIgnore
    private Long roomId;
    // 유저 번호
    private Long userNo;
}
