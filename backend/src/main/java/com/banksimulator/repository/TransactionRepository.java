package com.banksimulator.repository;

import com.banksimulator.entity.Account;
import com.banksimulator.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 거래 내역 레포지토리 - 거래 내역 데이터베이스 처리
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 특정 계좌의 거래 내역을 최신 순으로 조회
    List<Transaction> findByAccountOrderByCreatedAtDesc(Account account);
}
