package com.banksimulator.service;

import com.banksimulator.dto.request.TransferRequest;
import com.banksimulator.dto.response.TransactionResponse;
import com.banksimulator.entity.Account;
import com.banksimulator.entity.Transaction;
import com.banksimulator.entity.User;
import com.banksimulator.exception.InsufficientBalanceException;
import com.banksimulator.exception.InvalidRequestException;
import com.banksimulator.exception.ResourceNotFoundException;
import com.banksimulator.exception.UnauthorizedAccessException;
import com.banksimulator.repository.AccountRepository;
import com.banksimulator.repository.TransactionRepository;
import com.banksimulator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 송금/거래 서비스 - 송금과 거래 내역 조회 비즈니스 로직 처리
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    /**
     * 계좌 간 송금 처리 (트랜잭션 원자성 보장)
     * @Transactional 어노테이션으로 송금 과정이 하나의 DB 트랜잭션으로 처리됨
     * 중간에 오류 발생 시 전체 롤백됨
     */
    @Transactional
    public void transfer(String email, TransferRequest request) {
        User user = findUserByEmail(email);

        // 출금 계좌와 입금 계좌가 같은 경우 예외 처리
        if (request.getFromAccountNumber().equals(request.getToAccountNumber())) {
            throw new InvalidRequestException("출금 계좌와 수신 계좌가 동일합니다");
        }

        // 출금 계좌 조회
        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "출금 계좌를 찾을 수 없습니다: " + request.getFromAccountNumber()));

        // 출금 계좌 소유자 확인
        if (!fromAccount.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("본인의 계좌에서만 송금할 수 있습니다");
        }

        // 수신 계좌 조회
        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "수신 계좌를 찾을 수 없습니다: " + request.getToAccountNumber()));

        BigDecimal amount = request.getAmount();

        // 잔액 부족 확인
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    String.format("잔액이 부족합니다. 현재 잔액: %s원, 송금 금액: %s원",
                            fromAccount.getBalance(), amount));
        }

        // 잔액 차감 및 증가 (원자적 처리)
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        String description = request.getDescription() != null ? request.getDescription()
                : toAccount.getUser().getName() + " 계좌로 송금";

        // 출금 계좌 거래 내역 기록 (TRANSFER_OUT)
        Transaction outTransaction = Transaction.builder()
                .type(Transaction.TransactionType.TRANSFER_OUT)
                .amount(amount)
                .balanceAfter(fromAccount.getBalance())
                .description(description)
                .counterpartAccountNumber(toAccount.getAccountNumber())
                .account(fromAccount)
                .build();

        // 입금 계좌 거래 내역 기록 (TRANSFER_IN)
        Transaction inTransaction = Transaction.builder()
                .type(Transaction.TransactionType.TRANSFER_IN)
                .amount(amount)
                .balanceAfter(toAccount.getBalance())
                .description(fromAccount.getUser().getName() + "님으로부터 수신")
                .counterpartAccountNumber(fromAccount.getAccountNumber())
                .account(toAccount)
                .build();

        transactionRepository.save(outTransaction);
        transactionRepository.save(inTransaction);
    }

    /**
     * 계좌별 거래 내역 조회 (최신 순)
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactions(String email, Long accountId) {
        User user = findUserByEmail(email);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("계좌를 찾을 수 없습니다: " + accountId));

        // 본인 계좌인지 확인
        if (!account.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("본인의 계좌 내역만 조회할 수 있습니다");
        }

        return transactionRepository.findByAccountOrderByCreatedAtDesc(account)
                .stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + email));
    }
}
