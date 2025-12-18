package com.jchat.chat.controller.ws;

import com.jchat.auth.dto.UserInfoDto;
import com.jchat.chat.dto.ChatRoomMsg;
import com.jchat.chat.dto.SendMsgReqDto;
import com.jchat.chat.dto.SendMsgResDto;
import com.jchat.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWsController {

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
    public SendMsgResDto sendMsg(
            @DestinationVariable Long roomId,
            @Payload SendMsgReqDto reqDto,
            SimpMessageHeaderAccessor headerAccessor
    ) {

        // 세션
        Map<String, Object> attrs = headerAccessor.getSessionAttributes();
        // userContext
        UserInfoDto userContext = (UserInfoDto) attrs.get("userInfo");

        Long userNo = userContext.getUserNo();
        String id = userContext.getId();

        ChatRoomMsg chatRoomMsg = chatService.sendMsg(userNo, roomId, reqDto);

        return SendMsgResDto.builder()
                .tempId(reqDto.getTempId())
                .roomId(roomId)
                .chatRoomMsg(chatRoomMsg)
                .build();
    }
}
