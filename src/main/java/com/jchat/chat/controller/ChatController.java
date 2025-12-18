package com.jchat.chat.controller;

import com.jchat.chat.dto.SearchChatRoomReqDto;
import com.jchat.chat.dto.SearchChatRoomResDto;
import com.jchat.chat.service.ChatService;
import com.jchat.common.context.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    /**
     * 채팅방 정보 조회
     * @param {{@link SearchChatRoomReqDto}} reqDto
     * @return {{@link SearchChatRoomResDto}}
     */
    @GetMapping("/searchChatRoom/{roomId}")
    public SearchChatRoomResDto searchChatRoom(@PathVariable Long roomId) {

        Long userNo = UserContext.getUserNo();

        System.out.println("roomId: " + roomId);

        System.out.println("userNo = " + userNo);

        SearchChatRoomReqDto reqDto = SearchChatRoomReqDto.builder()
                                                            .roomId(roomId)
                                                            .userNo(userNo)
                                                            .build();

        return chatService.searchChatRoom(reqDto);
    }

}
