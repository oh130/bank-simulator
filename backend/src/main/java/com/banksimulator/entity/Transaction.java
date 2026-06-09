package com.banksimulator.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 거래 내역 엔티티 - 입금/출금/송금 거래를 기록하는 테이블
 */
@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 거래 유형: DEPOSIT(입금), WITHDRAWAL(출금), TRANSFER_OUT(송금), TRANSFER_IN(수신)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    // 거래 금액
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    // 거래 후 잔액
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    // 거래 설명 (예: "홍길동 계좌로 송금")
    private String description;

    // 거래 상대방 계좌번호 (송금/수신 시 사용)
    private String counterpartAccountNumber;

    // 거래가 발생한 계좌 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // 거래 발생 시각
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * 거래 유형 열거형
     */
    public enum TransactionType {
        DEPOSIT,        // 입금
        WITHDRAWAL,     // 출금
        TRANSFER_OUT,   // 송금 (보내는 쪽)
        TRANSFER_IN     // 수신 (받는 쪽)
    }
}
