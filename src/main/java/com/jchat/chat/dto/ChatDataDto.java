package com.jchat.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatDataDto {
    private Long msgId;
    private Long roomId;
    private Long userNo;
    private String msgContent;
    private String msgTypCd;
    private String delYn;
    private String createTm;

    @Data
    public static class ChatRoomUser {
        private Long userNo; // 회원번호
        private String name; // 회원이름
        private String friendYn; // 친구여부
        private String mineYn; // 내정보여부
    }

    @Data
    public static class ChatRoomMsg {
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
}
