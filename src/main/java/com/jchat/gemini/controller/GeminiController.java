package com.jchat.gemini.controller;

import com.jchat.common.annotation.NoAuth;
import com.jchat.gemini.dto.TestReqDto;
import com.jchat.gemini.service.GeminiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gemini")
public class GeminiController {

    private final GeminiService geminiService;

    @NoAuth
    @GetMapping("/test")
    public String testController(@Valid TestReqDto reqDto) {
        String answer = geminiService.generateText(reqDto.getPrompt());

        System.out.println("===answer : " + answer);

        return answer;
    }
}
