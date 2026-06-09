package com.banksimulator.repository;

import com.banksimulator.entity.Account;
import com.banksimulator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 계좌 레포지토리 - 계좌 관련 데이터베이스 처리
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // 특정 회원의 모든 계좌 조회
    List<Account> findByUser(User user);

    // 계좌번호로 계좌 조회 (송금 시 상대방 계좌 찾기)
    Optional<Account> findByAccountNumber(String accountNumber);

    // 계좌번호 중복 확인
    boolean existsByAccountNumber(String accountNumber);
}
