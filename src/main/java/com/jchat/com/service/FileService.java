package com.jchat.com.service;

import com.jchat.com.dto.FileInfo;
import com.jchat.com.mapper.FileMapper;
import com.jchat.common.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final S3Client s3Client;
    private final FileMapper fileMapper;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;
    @Value("${cloudflare.r2.cdn-host}")
    private String cdnHost;

    public FileInfo uploadFile(MultipartFile file, String path) {
        try {
            // 고유 파일명 생성
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            String key = String.format("%s/%s", path, fileName);

            if (path.startsWith("/")) {
                // 패스 / 붙여서 온 경우
                key = key.substring(1);
            } else {
                // 패스 / 안붙여서 온 경우
                path = "/" + path;
            }

            long size = file.getSize();

            // 5MB
            if (size >= 5000000) {
                // 추후 리사이징 적용
                throw new RuntimeException("용량이 너무 큼");
            }

            // R2 전송 req
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            // R2에 업로드
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromBytes(file.getBytes()));

            Long userNo = UserContext.getUserNo();

            // 파일 테이블 req
            FileInfo fileInfo = FileInfo.builder()
                    .filePath(path)
                    .fileName(fileName)
                    .fileExt(StringUtils.getFilenameExtension(file.getOriginalFilename()))
                    .fileOriginalName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .userNo(userNo)
                    .build();

            // 파일 테이블 insert
            fileMapper.insertFile(fileInfo);

            // URL 반환
            return fileInfo;
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }
}