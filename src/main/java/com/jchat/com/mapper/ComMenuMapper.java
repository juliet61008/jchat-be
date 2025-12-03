package com.jchat.com.mapper;

import com.jchat.com.dto.ComMenuListSearchResDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ComMenuMapper {
    ComMenuListSearchResDto searchComMenuList();
}
