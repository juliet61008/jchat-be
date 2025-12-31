package com.jchat.chat.mapper;

import com.jchat.chat.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatMapper {
    void insertChatRoom(InsertChatRoomReqDto insertChatRoomReqDto ); // 채팅방 베이스 삽입
    void insertChatRoomUser(InsertChatRoomUserReqDto insertChatRoomUserReqDto); // 채팅방 유저 삽입
    Long selectChatRoomMsgSeq(); // 채팅방 메세지 시퀀스
    void insertChatRoomMsg(InsertChatRoomMsgReqDto insertChatRoomMsgReqDto); // 채팅방 메세지 삽입
    ChatRoomMsg searchChatRoomMsgByPk (SearchChatRoomMsgByPkReqDto searchChatRoomMsgByPkReqDto);
    ChatRoom searchChatRoomBasInfo(SearchChatRoomReqDto searchChatRoomReqDto); // 채팅방 기본 정보 조회
    List<ChatRoomUser> searchChatRoomUser(SearchChatRoomReqDto searchChatRoomReqDto); // 채팅방 참여자 목록 조회
    List<ChatRoomMsg> searchChatRoomMsg(SearchChatRoomReqDto searchChatRoomReqDto); // 채팅방 메세지 정보 조회
}
