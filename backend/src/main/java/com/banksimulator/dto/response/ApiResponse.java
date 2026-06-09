package com.banksimulator.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 공통 API 응답 래퍼 - 모든 응답을 일관된 형식으로 반환
 * { "success": true, "message": "...", "data": {...} }
 */
@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    // 성공 응답 (데이터 포함)
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    // 성공 응답 (데이터 없음)
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    // 실패 응답
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
