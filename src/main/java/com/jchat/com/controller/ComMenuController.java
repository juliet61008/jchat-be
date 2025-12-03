package com.jchat.com.controller;

import com.jchat.com.dto.ComMenuListSearchResDto;
import com.jchat.com.service.ComMenuService;
import com.jchat.common.annotation.NoAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/com")
public class ComMenuController {

    private final ComMenuService comMenuService;

    @NoAuth
    @GetMapping("/searchComMenuList")
    private ComMenuListSearchResDto searchComMenuList() {
        return comMenuService.searchComMenuList();
    }
}
