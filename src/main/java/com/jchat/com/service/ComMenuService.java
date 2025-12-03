package com.jchat.com.service;

import com.jchat.com.dto.ComMenuListSearchResDto;
import com.jchat.com.mapper.ComMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ComMenuService {
    private final ComMenuMapper comMenuMapper;

    public ComMenuListSearchResDto searchComMenuList() {
        return comMenuMapper.searchComMenuList();
    }
}
