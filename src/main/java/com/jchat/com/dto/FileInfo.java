package com.jchat.com.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileInfo {
    private Long fileNo; // 파일테이블 key
    private String delYn; // 삭제여부
    private String filePath; // 파일경로
    private String fileName; // 파일이름
    private String fileExt; // 파일확장자
    private String fileOriginalName; // 파일원본이름
    private Long userNo; // 회원번호
    private Long fileSize; // 파일사이즈 byte
}
