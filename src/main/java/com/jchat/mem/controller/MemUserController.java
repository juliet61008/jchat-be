package com.jchat.mem.controller;

import com.jchat.common.annotation.NoAuth;
import com.jchat.common.context.UserContext;
import com.jchat.mem.dto.*;
import com.jchat.mem.service.MemUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mem/user")
public class MemUserController {

    private final MemUserService memUserService;

    /**
     * @description 회원정보조회
     * @param {{@link String}}
     */
    @NoAuth
    @GetMapping("/searchUser/{id}")
    public void searchUser(@PathVariable String id) {
        SearchUserReqDto reqDto = new SearchUserReqDto().builder().id(id).build();
        SearchUserResDto resDto = memUserService.searchUser(reqDto);
        log.info("id : {} , name : {}", resDto.getId(), resDto.getName());
    }

    @GetMapping("/searchUserList")
    public List<SearchUserListResDto> searchUserList() {

        return memUserService.searchUserList(SearchUserListReqDto.builder().userNo(UserContext.getUserNo()).build());
    }

    /**
     * @description 회원가입
     * @param {{@link RegisterUserReqDto}} reqDto
     * @return {{@link RegisterUserResDto}} resDto
     */
    @NoAuth
    @PostMapping("/registerUser")
    public RegisterUserResDto registerUser(@Valid @RequestBody RegisterUserReqDto reqDto) {

        return memUserService.registerUser(reqDto);
    }
}
