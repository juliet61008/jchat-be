package com.jchat.com.controller;

import com.jchat.common.annotation.NoAuth;
import com.jchat.com.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    @NoAuth
    @PostMapping("/upload")
    public Object uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("path") String path) {
        return fileService.uploadFile(file, path);
    }
}