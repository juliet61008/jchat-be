package com.jchat.chat.controller.ws;

import com.jchat.auth.dto.UserInfoDto;
import com.jchat.chat.dto.ChatRoomMsg;
import com.jchat.chat.dto.SendMsgReqDto;
import com.jchat.chat.dto.SendMsgResDto;
import com.jchat.chat.service.ChatService;
import com.jchat.chat.util.ChatSequenceProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWsController {

    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatSequenceProvider chatSequenceProvider;

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
    public void sendMsg(
            @DestinationVariable Long roomId,
            @Payload SendMsgReqDto reqDto,
            SimpMessageHeaderAccessor headerAccessor
    ) {

        // 채팅방번호 null로 오는 경우
        if (roomId == null) {
            // 첫요청으로 판단 ::: 채팅방 생성
            chatService.generateRoom(roomId, reqDto);
        }

        // 메세지시퀀스 조회
        Long chatRoomMsgSeq = chatSequenceProvider.getNext();

        // 세션
        Map<String, Object> attrs = headerAccessor.getSessionAttributes();
        // userContext
        UserInfoDto userContext = (UserInfoDto) attrs.get("userInfo");

        // 메세지 응답 전 resDto 먼저 생성
        ChatRoomMsg chatRoomMsg = chatService.generateChatRoomMsgBeforSend(userContext, chatRoomMsgSeq, reqDto);

        // 메세지 선발행
        simpMessagingTemplate.convertAndSend("/topic/chat/send/" + roomId, SendMsgResDto.builder()
                .tempId(reqDto.getTempId())
                .roomId(roomId)
                .chatRoomMsg(chatRoomMsg)
                .build()
                );

        Long userNo = userContext.getUserNo();

        // 메세지 발송 정보 비동기 저장
        chatService.sendMsg(userNo, roomId, chatRoomMsgSeq, reqDto);

//        return SendMsgResDto.builder()
//                .tempId(reqDto.getTempId())
//                .roomId(roomId)
//                .chatRoomMsg(chatRoomMsg)
//                .build();
    }
}
