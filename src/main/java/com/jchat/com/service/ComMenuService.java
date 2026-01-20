package com.jchat.com.service;

import com.jchat.com.dto.ComMenuListSearchResDto;
import com.jchat.com.mapper.ComMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComMenuService {
    private final ComMenuMapper comMenuMapper;

    public List<ComMenuListSearchResDto> searchComMenuList() {
        return comMenuMapper.searchComMenuList();
    }
}
