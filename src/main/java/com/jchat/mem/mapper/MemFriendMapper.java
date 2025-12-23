package com.jchat.mem.mapper;

import com.jchat.mem.dto.ComOtherUser;
import com.jchat.mem.dto.InsertFriendReqDto;
import com.jchat.mem.dto.SearchUserListResDto;
import com.jchat.mem.dto.UpdateFriendReqDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MemFriendMapper {
    List<ComOtherUser> searchFriendList(Long userNo);
    abstract int insertFriend(InsertFriendReqDto insertFriendReqDto);
    abstract int updateFriend(UpdateFriendReqDto updateFriendReqDto);
}
