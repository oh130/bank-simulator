package com.banksimulator.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 송금 요청 DTO
 */
@Getter
@Setter
public class TransferRequest {

    // 보내는 계좌번호 (내 계좌)
    @NotBlank(message = "출금 계좌번호는 필수입니다")
    private String fromAccountNumber;

    // 받는 계좌번호
    @NotBlank(message = "수신 계좌번호는 필수입니다")
    private String toAccountNumber;

    // 송금 금액 (최소 1원)
    @NotNull(message = "송금 금액은 필수입니다")
    @DecimalMin(value = "1.0", message = "송금 금액은 최소 1원 이상이어야 합니다")
    private BigDecimal amount;

    // 송금 메모 (선택)
    private String description;
}
