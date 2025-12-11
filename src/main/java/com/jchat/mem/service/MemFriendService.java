package com.jchat.mem.service;

import com.jchat.mem.dto.SearchUserListReqDto;
import com.jchat.mem.dto.SearchUserListResDto;
import com.jchat.mem.dto.SearchUserReqDto;
import com.jchat.mem.dto.SearchUserResDto;
import com.jchat.mem.mapper.MemFriendMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemFriendService {

    private final MemFriendMapper memFriendMapper;

    /**
     * 회원정보조회
     * @param {{@link SearchUserReqDto}} reqDto
     * @return {{@link SearchUserResDto}} resDto
     */
    public List<SearchUserListResDto> searchUserList(SearchUserListReqDto reqDto) {



        return null;
    }

}
