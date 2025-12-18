package com.jchat.chat.service;

import com.jchat.chat.dto.*;
import com.jchat.chat.mapper.ChatMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMapper chatMapper;

    /**
     * 메세지 발송
     * @param {{@link Long}} roomId 채팅방번호
     * @param {{@link SendMsgReqDto}} sendMsgReqDto
     * @return {{@link SendMsgResDto}0}
     */
    @Transactional
    public ChatRoomMsg sendMsg(Long userNo, Long roomId, SendMsgReqDto sendMsgReqDto) {

        // 채팅방번호 null로 오는 경우
        if (roomId == null) {
            // 첫요청으로 판단 ::: 채팅방 생성
            generateRoom(roomId, sendMsgReqDto);
        }

        // 채팅 메세지 생성
        InsertChatRoomMsgReqDto insertChatRoomMsgReqDto = InsertChatRoomMsgReqDto.builder()
                .roomId(roomId)
                .userNo(userNo) // 여기 userNo는 토큰에서 가져와야할듯
                .msgContent(sendMsgReqDto.getMsgContent())
                .build();

        // 채팅 메세지 삽입 요청
        chatMapper.insertChatRoomMsg(insertChatRoomMsgReqDto);

        // 메세지 시퀀스
        Long msgId = insertChatRoomMsgReqDto.getMsgId();

        SearchChatRoomMsgByPkReqDto searchChatRoomMsgByPkReqDto = SearchChatRoomMsgByPkReqDto.builder().msgId(msgId).roomId(roomId).userNo(userNo).build();

        ChatRoomMsg chatRoomMsg = chatMapper.searchChatRoomMsgByPk(searchChatRoomMsgByPkReqDto);

        return chatRoomMsg;
    }

    /**
     * 채팅방+채팅방유저+채팅메세지 생성 메서드 (첫요청)
     * @param {{@link SendMsgReqDto}} sendMsgReqDto
     */
    public void generateRoom(Long roomId, SendMsgReqDto sendMsgReqDto) {
        // 채팅방 생성
        InsertChatRoomReqDto insertChatRoomReqDto = InsertChatRoomReqDto.builder()
                .roomName(sendMsgReqDto.getRoomName())
                .build();
        // 채팅방 삽입 요청
        chatMapper.insertChatRoom(insertChatRoomReqDto);

        roomId = insertChatRoomReqDto.getRoomId();

        // 채팅방 유저 생성
        for (SendMsgReqDto.InviteUserListDto inviteUser : sendMsgReqDto.getInviteUserList()) {
            InsertChatRoomUserReqDto insertChatRoomUserReqDto = InsertChatRoomUserReqDto.builder()
                    .roomId(roomId)
                    .userNo(inviteUser.getUserNo())
                    .build();
            // 채팅방 유저 삽입 요청
            chatMapper.insertChatRoomUser(insertChatRoomUserReqDto);
        }
    }

    /**
     * 채팅방정보 조회
     * @param reqDto
     * @return
     */
    public SearchChatRoomResDto searchChatRoom(SearchChatRoomReqDto reqDto) {

        // 유저리스트 조회
        List<ChatRoomUser> chatRoomUser = chatMapper.searchChatRoomUser(reqDto);
        // 채팅방메세지리스트 조회
        List<ChatRoomMsg> chatRoomMsg = chatMapper.searchChatRoomMsg(reqDto);

        return SearchChatRoomResDto.builder()
                .roomId(reqDto.getRoomId())
                .chatRoomUserList(chatRoomUser)
                .chatRoomMsgList(chatRoomMsg)
                .build();
    }
}
