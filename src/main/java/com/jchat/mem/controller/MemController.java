package com.jchat.mem.controller;

import com.jchat.common.annotation.NoAuth;
import com.jchat.mem.dto.SearchUserReqDto;
import com.jchat.mem.dto.SearchUserResDto;
import com.jchat.mem.service.MemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mem")
public class MemController {

    private final MemService memService;

    @NoAuth
    @GetMapping("/searchUser/{id}")
    public void searchUser(@PathVariable String id) {
        SearchUserReqDto reqDto = new SearchUserReqDto().builder().id(id).build();
        SearchUserResDto resDto = memService.searchUser(reqDto);
        System.out.println("==================");
        log.info("id : {} , name : {}", resDto.getId(), resDto.getName());
    }
}
