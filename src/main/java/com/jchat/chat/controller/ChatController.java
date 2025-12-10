package com.jchat.chat.controller;

import com.jchat.chat.dto.SendMsgReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * 테스트 컨트롤러
     * @param message
     * @return
     */
    @MessageMapping("/hello")
    @SendTo("/topic/wsTest")
    public String wsTest(String message) {
        System.out.println("받은거: " + message);
        return "응답할거: " + message + " 이거 보낸거 맞지?";
    }

    /**
     * 메세지 발송
     * @param roomId
     * @param reqDto
     */
    @MessageMapping("/chat/send/{roomId}")
    @SendTo("/topic/chat/send/{roomId}")
    public void sendMsg(
            @DestinationVariable Long roomId,
            @Payload SendMsgReqDto reqDto
    ) {
        chatService.sendMsg(roomId, reqDto);
    }
}
