package com.jchat.chat.mapper;

import com.jchat.chat.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatMapper {
    void insertChatRoom(InsertChatRoomReqDto insertChatRoomReqDto );
    void insertChatRoomUser(InsertChatRoomUserReqDto insertChatRoomUserReqDto);
    void insertChatRoomMsg(InsertChatRoomMsgReqDto insertChatRoomMsgReqDto);
    ChatRoomMsg searchChatRoomMsgByPk (SearchChatRoomMsgByPkReqDto searchChatRoomMsgByPkReqDto);
    List<ChatRoomUser> searchChatRoomUser(SearchChatRoomReqDto searchChatRoomReqDto);
    List<ChatRoomMsg> searchChatRoomMsg(SearchChatRoomReqDto searchChatRoomReqDto);
}
