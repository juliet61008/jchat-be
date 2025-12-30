package com.jchat.mem.mapper;

import com.jchat.mem.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MemFriendMapper {
    List<ComOtherUser> searchFriendList(Long userNo);
    int mergeFriend(MergeFriendReqDto mergeFriendReqDto);
}
