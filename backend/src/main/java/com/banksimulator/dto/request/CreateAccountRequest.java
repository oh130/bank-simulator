package com.banksimulator.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 계좌 개설 요청 DTO
 */
@Getter
@Setter
public class CreateAccountRequest {

    // 초기 입금액 (최소 0원 이상)
    @NotNull(message = "초기 잔액은 필수입니다")
    @DecimalMin(value = "0.0", message = "초기 잔액은 0원 이상이어야 합니다")
    private BigDecimal initialBalance;
}
