package com.jchat.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공통 REST API 결과
 * @param <T>
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private int code;  // String -> int
    private String message;
    private T data;

    // 성공 응답 (code = 0)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "요청이 성공했습니다.", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(0, message, data);
    }

    // 실패 응답 (code = -1)
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(-1, message, null);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}