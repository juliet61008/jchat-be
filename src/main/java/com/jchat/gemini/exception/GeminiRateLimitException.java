package com.jchat.gemini.exception;

import lombok.Getter;

@Getter
public class GeminiRateLimitException extends GeminiApiException {
    private final int retryAfterSeconds;

    public GeminiRateLimitException(String message, int retryAfterSeconds) {
        super(message, 429);
        this.retryAfterSeconds = retryAfterSeconds;
    }
}