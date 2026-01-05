package com.jchat.common.advice;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final int code;

    public CustomException(String message) {
        super(message);
        this.code = -1;
    }

    public CustomException(int code, String message) {
        super(message);
        this.code = code;
    }

    public CustomException(int code) {
        super("");
        this.code = code;
    }
}
