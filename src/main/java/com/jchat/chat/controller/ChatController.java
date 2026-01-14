package com.jchat.chat.controller;

import com.jchat.chat.dto.SearchChatRoomDtlReqDto;
import com.jchat.chat.dto.SearchChatRoomDtlResDto;
import com.jchat.chat.dto.SearchChatRoomListResDto;
import com.jchat.chat.service.ChatService;
import com.jchat.common.advice.CustomException;
import com.jchat.common.context.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    /**
     * 채팅방 리스트 정보 조회
     */
    @GetMapping("/room")
    public List<SearchChatRoomListResDto> searchChatRoomListResDto() {

        // 유저컨텍스트 없는 경우
        if (!UserContext.hasUser()) {
            throw new CustomException(-1, "유저 정보가 없습니다.");
        }

        List<SearchChatRoomListResDto> resDto = chatService.searhcChatRoomList();

        return resDto;
    }

    /**
     * 채팅방 디테일 정보 조회
     * @param roomId {{@link SearchChatRoomDtlReqDto}}
     * @return {{@link SearchChatRoomDtlResDto}}
     */
    @GetMapping("/room/{roomId}")
    public SearchChatRoomDtlResDto searchChatRoomDtl(@PathVariable Long roomId) {

        Long userNo = UserContext.getUserNo();

        SearchChatRoomDtlReqDto reqDto = SearchChatRoomDtlReqDto.builder()
                                                            .roomId(roomId)
                                                            .userNo(userNo)
                                                            .build();

        return chatService.searchChatRoomDtl(reqDto);
    }

}
