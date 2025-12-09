package com.jchat.mem.mapper;

import com.jchat.mem.dto.RegisterUserReqDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemUserMapper {
    int insertUser(RegisterUserReqDto reqDto);
}
