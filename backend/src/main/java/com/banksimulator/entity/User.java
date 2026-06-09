package com.banksimulator.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 회원 엔티티 - 사용자 정보를 저장하는 테이블
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 이름
    @Column(nullable = false)
    private String name;

    // 이메일 (로그인 ID로 사용, 중복 불가)
    @Column(nullable = false, unique = true)
    private String email;

    // BCrypt로 암호화된 비밀번호
    @Column(nullable = false)
    private String password;

    // 해당 회원의 계좌 목록 (1:N 관계)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Account> accounts = new ArrayList<>();

    // 생성 시각 (자동 저장)
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 수정 시각 (자동 갱신)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
