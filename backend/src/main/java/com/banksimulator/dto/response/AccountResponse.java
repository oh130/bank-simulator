package com.banksimulator.dto.response;

import com.banksimulator.entity.Account;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 계좌 정보 응답 DTO - 엔티티를 직접 노출하지 않고 필요한 필드만 반환
 */
@Getter
public class AccountResponse {
    private Long id;
    private String accountNumber;   // 계좌번호
    private BigDecimal balance;     // 현재 잔액
    private LocalDateTime createdAt;

    // 엔티티 → DTO 변환 정적 팩토리 메서드
    public static AccountResponse from(Account account) {
        AccountResponse dto = new AccountResponse();
        dto.id = account.getId();
        dto.accountNumber = account.getAccountNumber();
        dto.balance = account.getBalance();
        dto.createdAt = account.getCreatedAt();
        return dto;
    }
}
