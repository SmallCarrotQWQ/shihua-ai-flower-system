package com.shihua.common;

import java.time.Instant;

public record ApiResponse<T>(int code, String message, T data, long timestamp) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data, Instant.now().toEpochMilli());
    }

    public static <T> ApiResponse<T> failed(String message) {
        return new ApiResponse<>(500, message, null, Instant.now().toEpochMilli());
    }
}

