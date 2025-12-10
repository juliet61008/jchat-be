package com.jchat.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채팅방 메세지 insert DTO
 */

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class InsertChatRoomMsgReqDto {
    // 메세지번호
    @JsonIgnore
    private Long msgId;
    // 채팅방번호
    @JsonIgnore
    private Long roomId;
    // 유저번호
    private Long userNo;
    // 메세지 내용
    private String msgContent;
}
