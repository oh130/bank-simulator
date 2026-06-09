package com.banksimulator.repository;

import com.banksimulator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 회원 레포지토리 - 데이터베이스 CRUD 처리
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 회원 조회 (로그인 시 사용)
    Optional<User> findByEmail(String email);

    // 이메일 중복 확인
    boolean existsByEmail(String email);
}
