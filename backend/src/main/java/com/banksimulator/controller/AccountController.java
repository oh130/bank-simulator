package com.banksimulator.controller;

import com.banksimulator.dto.request.CreateAccountRequest;
import com.banksimulator.dto.response.AccountResponse;
import com.banksimulator.dto.response.ApiResponse;
import com.banksimulator.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 계좌 컨트롤러 - 계좌 개설, 목록 조회, 상세 조회 API 제공
 * 기본 경로: /api/v1/accounts
 * 모든 API는 JWT 인증 필요
 */
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * POST /api/v1/accounts - 계좌 개설
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateAccountRequest request) {

        AccountResponse account = accountService.createAccount(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("계좌가 개설되었습니다", account));
    }

    /**
     * GET /api/v1/accounts - 내 계좌 목록 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getMyAccounts(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<AccountResponse> accounts = accountService.getMyAccounts(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("계좌 목록 조회 성공", accounts));
    }

    /**
     * GET /api/v1/accounts/{id} - 계좌 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountDetail(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        AccountResponse account = accountService.getAccountDetail(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success("계좌 상세 조회 성공", account));
    }
}
