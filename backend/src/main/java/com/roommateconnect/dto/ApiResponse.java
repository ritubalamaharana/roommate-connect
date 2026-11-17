package com.roommateconnect.dto;

import java.time.Instant;

/**
 * Standard wrapper for every API response ("consistent API response
 * structures" in the non-functional requirements).
 */
public record ApiResponse<T>(boolean success, String message, T data, Instant timestamp) {

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, Instant.now());
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data, Instant.now());
    }
}