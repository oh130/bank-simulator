package com.banksimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 은행 계좌/송금 시뮬레이터 메인 애플리케이션 클래스
 */
@SpringBootApplication
public class BankSimulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankSimulatorApplication.class, args);
    }
}
