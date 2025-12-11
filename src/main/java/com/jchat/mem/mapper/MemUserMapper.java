package com.jchat.mem.mapper;

import com.jchat.mem.dto.RegisterUserReqDto;
import com.jchat.mem.dto.SearchUserListReqDto;
import com.jchat.mem.dto.SearchUserListResDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemUserMapper {
    List<SearchUserListResDto> searchUserList(SearchUserListReqDto reqDto);
    int insertUser(RegisterUserReqDto reqDto);
}
