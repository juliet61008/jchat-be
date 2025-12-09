package com.jchat.mem.controller;

import com.jchat.common.annotation.NoAuth;
import com.jchat.mem.dto.RegisterUserReqDto;
import com.jchat.mem.dto.RegisterUserResDto;
import com.jchat.mem.dto.SearchUserReqDto;
import com.jchat.mem.dto.SearchUserResDto;
import com.jchat.mem.service.MemUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mem")
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
        System.out.println("==================");
        log.info("id : {} , name : {}", resDto.getId(), resDto.getName());
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
