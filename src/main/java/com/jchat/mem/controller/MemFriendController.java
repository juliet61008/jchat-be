package com.jchat.mem.controller;

import com.jchat.common.context.UserContext;
import com.jchat.mem.dto.SearchUserListReqDto;
import com.jchat.mem.dto.SearchUserListResDto;
import com.jchat.mem.service.MemFriendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mem/friend/")
public class MemFriendController {

    private final MemFriendService memFriendService;

    @GetMapping("/searchFreinds")
    public List<SearchUserListResDto> searchFreinds() {

        return memFriendService.searchUserList(SearchUserListReqDto.builder().userNo(UserContext.getUserNo()).build());
    }
}
