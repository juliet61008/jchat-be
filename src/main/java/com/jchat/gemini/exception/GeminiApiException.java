package com.jchat.gemini.exception;

import lombok.Getter;

@Getter
public class GeminiApiException extends RuntimeException {
    private final int statusCode;

    public GeminiApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}