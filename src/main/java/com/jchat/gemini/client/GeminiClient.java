package com.jchat.gemini.client;

import com.jchat.gemini.config.GeminiClientConfig;
import com.jchat.gemini.dto.GeminiRequest;
import com.jchat.gemini.dto.GeminiResponse;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "gemini-client",
        url = "${gemini.base-url}",
        configuration = GeminiClientConfig.class
)
public interface GeminiClient {

    /**
     * Gemini 2.5 Flash - 추천 (10 RPM, 250 RPD)
     */
    @PostMapping("/models/gemini-2.5-flash:generateContent")
    GeminiResponse generateContent(
            @RequestParam("key") String apiKey,
            @RequestBody GeminiRequest request
    );

    /**
     * Gemini 2.5 Pro - 복잡한 추론 (5 RPM, 100 RPD)
     */
    @PostMapping("/models/gemini-2.5-pro:generateContent")
    GeminiResponse generateContentPro(
            @RequestParam("key") String apiKey,
            @RequestBody GeminiRequest request
    );

    /**
     * Gemini 2.5 Flash Lite - 빠른 처리 (15 RPM, 1000 RPD)
     */
    @PostMapping("/models/gemini-2.5-flash-lite:generateContent")
    GeminiResponse generateContentLite(
            @RequestParam("key") String apiKey,
            @RequestBody GeminiRequest request
    );
}