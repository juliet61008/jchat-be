package com.jchat.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SendMsgResDto {
    private Long roomId;
    private Long msgId;
    private ChatDataDto chatDataDto;
}
