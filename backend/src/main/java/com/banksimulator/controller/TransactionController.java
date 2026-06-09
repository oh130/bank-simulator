package com.banksimulator.controller;

import com.banksimulator.dto.request.TransferRequest;
import com.banksimulator.dto.response.ApiResponse;
import com.banksimulator.dto.response.TransactionResponse;
import com.banksimulator.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 거래 컨트롤러 - 송금, 거래 내역 조회 API 제공
 * 기본 경로: /api/v1/transactions
 * 모든 API는 JWT 인증 필요
 */
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * POST /api/v1/transactions/transfer - 계좌 간 송금
     */
    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<Void>> transfer(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransferRequest request) {

        transactionService.transfer(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("송금이 완료되었습니다"));
    }

    /**
     * GET /api/v1/transactions/accounts/{accountId} - 계좌별 거래 내역 조회
     */
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long accountId) {

        List<TransactionResponse> transactions =
                transactionService.getTransactions(userDetails.getUsername(), accountId);
        return ResponseEntity.ok(ApiResponse.success("거래 내역 조회 성공", transactions));
    }
}
