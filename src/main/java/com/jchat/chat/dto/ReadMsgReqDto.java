package com.jchat.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 메세지 읽음 처리 요청 DTO
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReadMsgReqDto {

    // 채팅방 번호
    private Long roomId;
    // 유저 번호
    @JsonIgnore
    private Long userNo;
    // 마지막 조회 메세지 번호
    private Long lastReadMsgNo;
    // 마지막 조회 생성 날짜
    private LocalDateTime lastReadCreateTm;
}
