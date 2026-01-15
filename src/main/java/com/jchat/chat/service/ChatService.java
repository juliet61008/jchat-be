package com.jchat.chat.service;

import com.jchat.auth.dto.UserInfoDto;
import com.jchat.chat.dto.*;
import com.jchat.chat.mapper.ChatMapper;
import com.jchat.common.advice.CustomException;
import com.jchat.common.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMapper chatMapper;

    /**
     * 메세지 발송
     * @param {{@link Long}} roomId 채팅방번호
     * @param {{@link Long}} chatRoomMsgSeq 채팅메세지번호
     * @param {{@link SendMsgReqDto}} sendMsgReqDto
     * @return {{@link SendMsgResDto}0}
     */
    @Transactional
    @Async("sendMsgTaskExecutor")
    public void sendMsg(Long userNo, Long roomId, Long chatRoomMsgSeq, SendMsgReqDto sendMsgReqDto) {

        // 채팅 메세지 생성
        InsertChatRoomMsgReqDto insertChatRoomMsgReqDto = InsertChatRoomMsgReqDto.builder()
                .roomId(roomId)
                .msgId(chatRoomMsgSeq)
                .userNo(userNo)
                .msgContent(sendMsgReqDto.getMsgContent())
                .build();

        // 채팅 메세지 삽입 요청
        chatMapper.insertChatRoomMsg(insertChatRoomMsgReqDto);

        // 메세지 시퀀스
//        Long msgId = insertChatRoomMsgReqDto.getMsgId();

//        SearchChatRoomMsgByPkReqDto searchChatRoomMsgByPkReqDto = SearchChatRoomMsgByPkReqDto.builder().msgId(msgId).roomId(roomId).userNo(userNo).build();
//
//        ChatRoomMsg chatRoomMsg = chatMapper.searchChatRoomMsgByPk(searchChatRoomMsgByPkReqDto);
//
//        return chatRoomMsg;
    }

    @Transactional
    @Async("readMsgTaskExecutor")
    public void readMsg(ReadMsgReqDto readMsgReqDto) {
        chatMapper.upsertChatRoomRead(readMsgReqDto);
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
        sendMsgReqDto.setRoomId(roomId);

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
     * 채팅방 리스트 정보 조회
     */
    public List<SearchChatRoomListResDto> searhcChatRoomList() {

        return chatMapper.searchChatRoomList(UserContext.getUserNo());
    }

    /**
     * 채팅방 디테일 정보 조회
     * @param reqDto {{@link SearchChatRoomDtlReqDto}}
     * @return {{@link SearchChatRoomDtlResDto}}
     */
    public SearchChatRoomDtlResDto searchChatRoomDtl(SearchChatRoomDtlReqDto reqDto) {

        // 채팅방 기본 정보 조회
        ChatRoom chatRoom = chatMapper.searchChatRoomBasInfo(reqDto);
        // 유저리스트 조회
        List<ChatRoomUser> chatRoomUser = chatMapper.searchChatRoomUser(reqDto);
        // 채팅방메세지리스트 조회
        List<ChatRoomMsg> chatRoomMsg = chatMapper.searchChatRoomMsg(reqDto);

        if (chatRoom == null) throw new CustomException(-1, "채팅방이 조회되지 않았습니다.");

        return SearchChatRoomDtlResDto.builder()
                .roomId(reqDto.getRoomId())
                .chatRoom(chatRoom)
                .chatRoomUserList(chatRoomUser)
                .chatRoomMsgList(chatRoomMsg)
                .build();
    }

    /**
     * 메세지 응답 insert 전 resDto 먼저 만들기
     */
    public ChatRoomMsg generateChatRoomMsgBeforSend(UserInfoDto userContext, Long chatRoomMsgSeq, SendMsgReqDto reqDto) {
        return ChatRoomMsg.builder()
                .roomId(reqDto.getRoomId()) // 방번호
                .msgId(chatRoomMsgSeq) // 메세지번호
                .sndUserNo(userContext.getUserNo()) // 발신자 회원번호
                .sndName(userContext.getName()) // 발신자 성함
                .msgTypCd("01") // 메세지 타입
                .msgContent(reqDto.getMsgContent()) // 메세지 내용
                //.mineYn("")
                .delYn("N") // 삭제여부
                .createTm(LocalDateTime.now()) // 생성시간
                .build();
    }
}
