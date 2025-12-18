package com.jchat.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ChatRoomMsg {
    private Long roomId; // 채팅방번호
    private Long msgId; // 메세지번호
    private Long sndUserNo; // 발신자 회원번호
    private String sndName; // 발신자 성함
    private String msgTypCd; // 코드
    private String msgContent; // 메세지 내용
    private String mineYn; // 내정보여부
    private String delYn; // 삭제여부
    private LocalDateTime createTm; // 생성시간
}
