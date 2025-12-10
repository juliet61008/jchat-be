package com.jchat.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
