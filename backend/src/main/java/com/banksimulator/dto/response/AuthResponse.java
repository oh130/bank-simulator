package com.banksimulator.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 로그인/회원가입 성공 응답 DTO
 */
@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;       // JWT 액세스 토큰
    private String email;       // 사용자 이메일
    private String name;        // 사용자 이름
}
