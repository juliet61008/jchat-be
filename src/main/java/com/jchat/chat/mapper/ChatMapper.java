package com.jchat.chat.mapper;

import com.jchat.chat.dto.ChatDataDto;
import com.jchat.chat.dto.InsertChatRoomMsgReqDto;
import com.jchat.chat.dto.InsertChatRoomReqDto;
import com.jchat.chat.dto.InsertChatRoomUserReqDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMapper {
    void insertChatRoom(InsertChatRoomReqDto insertChatRoomReqDto );
    void insertChatRoomUser(InsertChatRoomUserReqDto insertChatRoomUserReqDto);
    void insertChatRoomMsg(InsertChatRoomMsgReqDto insertChatRoomMsgReqDto);
    ChatDataDto searchChatRoomMsgByPk (Long msgId);
}
