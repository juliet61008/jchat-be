package com.jchat.com.mapper;

import com.jchat.com.dto.ComMenuListSearchResDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ComMenuMapper {
    List<ComMenuListSearchResDto> searchComMenuList();
}
