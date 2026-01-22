package com.jchat.gemini.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jchat.gemini.exception.GeminiApiException;
import com.jchat.gemini.exception.GeminiRateLimitException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class GeminiErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = "Gemini API 오류";

        try {
            if (response.body() != null) {
                InputStream bodyIs = response.body().asInputStream();
                String body = new String(bodyIs.readAllBytes());
                log.error("Gemini API Error Response: {}", body);
                message = body;
            }
        } catch (IOException e) {
            log.error("Error reading response body", e);
        }

        return switch (response.status()) {
            case 400 -> new GeminiApiException(
                    "잘못된 요청입니다: " + message, 400
            );
            case 401 -> new GeminiApiException(
                    "API 키가 유효하지 않습니다", 401
            );
            case 403 -> new GeminiApiException(
                    "API 접근이 거부되었습니다", 403
            );
            case 429 -> {
                String retryAfter = response.headers()
                        .getOrDefault("Retry-After", java.util.List.of("60"))
                        .stream().findFirst().orElse(null);
                yield new GeminiRateLimitException(
                        "Rate Limit 초과. " + retryAfter + "초 후 재시도하세요",
                        Integer.parseInt(retryAfter)
                );
            }
            case 500 -> new GeminiApiException(
                    "Gemini API 서버 오류입니다", 500
            );
            case 503 -> new GeminiApiException(
                    "Gemini API 서비스를 일시적으로 사용할 수 없습니다", 503
            );
            default -> defaultErrorDecoder.decode(methodKey, response);
        };
    }
}