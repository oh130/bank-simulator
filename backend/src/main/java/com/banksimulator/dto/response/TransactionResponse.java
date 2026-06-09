package com.banksimulator.dto.response;

import com.banksimulator.entity.Transaction;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 거래 내역 응답 DTO
 */
@Getter
public class TransactionResponse {
    private Long id;
    private String type;                        // 거래 유형 (DEPOSIT, WITHDRAWAL, TRANSFER_OUT, TRANSFER_IN)
    private String typeLabel;                   // 한국어 거래 유형 (입금, 출금, 송금, 수신)
    private BigDecimal amount;                  // 거래 금액
    private BigDecimal balanceAfter;            // 거래 후 잔액
    private String description;                 // 거래 설명
    private String counterpartAccountNumber;    // 상대방 계좌번호
    private LocalDateTime createdAt;            // 거래 일시

    // 엔티티 → DTO 변환 정적 팩토리 메서드
    public static TransactionResponse from(Transaction transaction) {
        TransactionResponse dto = new TransactionResponse();
        dto.id = transaction.getId();
        dto.type = transaction.getType().name();
        dto.typeLabel = getKoreanLabel(transaction.getType());
        dto.amount = transaction.getAmount();
        dto.balanceAfter = transaction.getBalanceAfter();
        dto.description = transaction.getDescription();
        dto.counterpartAccountNumber = transaction.getCounterpartAccountNumber();
        dto.createdAt = transaction.getCreatedAt();
        return dto;
    }

    // 거래 유형 한국어 변환
    private static String getKoreanLabel(Transaction.TransactionType type) {
        return switch (type) {
            case DEPOSIT -> "입금";
            case WITHDRAWAL -> "출금";
            case TRANSFER_OUT -> "송금";
            case TRANSFER_IN -> "수신";
        };
    }
}
