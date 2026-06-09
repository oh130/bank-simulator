package com.banksimulator.service;

import com.banksimulator.dto.request.CreateAccountRequest;
import com.banksimulator.dto.response.AccountResponse;
import com.banksimulator.entity.Account;
import com.banksimulator.entity.Transaction;
import com.banksimulator.entity.User;
import com.banksimulator.exception.ResourceNotFoundException;
import com.banksimulator.exception.UnauthorizedAccessException;
import com.banksimulator.repository.AccountRepository;
import com.banksimulator.repository.TransactionRepository;
import com.banksimulator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 계좌 서비스 - 계좌 개설, 조회 비즈니스 로직 처리
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    /**
     * 계좌 개설
     * 1. 현재 로그인한 사용자 조회
     * 2. 고유 계좌번호 생성
     * 3. 계좌 생성 및 초기 잔액 설정
     * 4. 초기 입금 거래 내역 기록
     */
    @Transactional
    public AccountResponse createAccount(String email, CreateAccountRequest request) {
        User user = findUserByEmail(email);

        // 중복되지 않는 계좌번호 생성
        String accountNumber = generateUniqueAccountNumber();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .balance(request.getInitialBalance())
                .user(user)
                .build();

        accountRepository.save(account);

        // 초기 입금액이 0보다 크면 DEPOSIT 거래 내역 기록
        if (request.getInitialBalance().compareTo(java.math.BigDecimal.ZERO) > 0) {
            Transaction initialDeposit = Transaction.builder()
                    .type(Transaction.TransactionType.DEPOSIT)
                    .amount(request.getInitialBalance())
                    .balanceAfter(request.getInitialBalance())
                    .description("계좌 개설 초기 입금")
                    .account(account)
                    .build();
            transactionRepository.save(initialDeposit);
        }

        return AccountResponse.from(account);
    }

    /**
     * 내 계좌 목록 조회
     */
    @Transactional(readOnly = true)
    public List<AccountResponse> getMyAccounts(String email) {
        User user = findUserByEmail(email);
        return accountRepository.findByUser(user)
                .stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 계좌 상세 조회 (본인 계좌인지 확인 포함)
     */
    @Transactional(readOnly = true)
    public AccountResponse getAccountDetail(String email, Long accountId) {
        User user = findUserByEmail(email);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("계좌를 찾을 수 없습니다: " + accountId));

        // 본인 계좌인지 확인
        if (!account.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("본인의 계좌만 조회할 수 있습니다");
        }

        return AccountResponse.from(account);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + email));
    }

    /**
     * 중복되지 않는 10자리 계좌번호 생성
     * 형식: 100-XXXXXXX (앞 3자리 고정, 뒤 7자리 랜덤)
     */
    private String generateUniqueAccountNumber() {
        Random random = new Random();
        String accountNumber;
        do {
            // 100-0000000 ~ 100-9999999 형식
            int number = random.nextInt(9_000_000) + 1_000_000;
            accountNumber = "100-" + number;
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
