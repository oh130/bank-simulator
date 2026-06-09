package com.banksimulator.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 계좌 엔티티 - 은행 계좌 정보를 저장하는 테이블
 */
@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 계좌번호 (자동 생성, 중복 불가)
    @Column(nullable = false, unique = true)
    private String accountNumber;

    // 현재 잔액
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    // 계좌 소유자 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 이 계좌의 거래 내역 목록 (1:N 관계)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    // 생성 시각
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 수정 시각
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
