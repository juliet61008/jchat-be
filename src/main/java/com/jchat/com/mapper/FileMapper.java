package com.jchat.com.mapper;

import com.jchat.com.dto.FileInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper {
    int insertFile(FileInfo fileInfo);
    int updateFile(FileInfo fileInfo);
    FileInfo selectFile(FileInfo fileInfo);
}
