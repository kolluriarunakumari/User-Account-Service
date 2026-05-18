package com.microservice.config;

import com.microservice.model.Account;
import com.microservice.model.User;
import com.microservice.repository.AccountRepository;
import com.microservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Loading sample data...");

            // --- USERS ---
            User u1 = userRepository.save(User.builder()
                    .userId("USR-10001").fullName("Alice Johnson")
                    .email("alice.johnson@example.com").phoneNumber("+1-555-0101")
                    .status("ACTIVE").createdAt(LocalDateTime.now()).build());

            User u2 = userRepository.save(User.builder()
                    .userId("USR-10002").fullName("Bob Williams")
                    .email("bob.williams@example.com").phoneNumber("+1-555-0102")
                    .status("ACTIVE").createdAt(LocalDateTime.now()).build());

            User u3 = userRepository.save(User.builder()
                    .userId("USR-10003").fullName("Carol Martinez")
                    .email("carol.martinez@example.com").phoneNumber("+1-555-0103")
                    .status("ACTIVE").createdAt(LocalDateTime.now()).build());

            User u4 = userRepository.save(User.builder()
                    .userId("USR-10004").fullName("David Lee")
                    .email("david.lee@example.com").phoneNumber("+1-555-0104")
                    .status("INACTIVE").createdAt(LocalDateTime.now()).build());

            User u5 = userRepository.save(User.builder()
                    .userId("USR-10005").fullName("Emma Davis")
                    .email("emma.davis@example.com").phoneNumber("+1-555-0105")
                    .status("ACTIVE").createdAt(LocalDateTime.now()).build());

            // --- ACCOUNTS ---
            accountRepository.save(Account.builder()
                    .accountNumber("ACC-20001001").accountType("SAVINGS")
                    .balance(new BigDecimal("12500.00")).status("ACTIVE")
                    .userId(u1.getUserId()).createdAt(LocalDateTime.now()).build());

            accountRepository.save(Account.builder()
                    .accountNumber("ACC-20001002").accountType("CHECKING")
                    .balance(new BigDecimal("3200.50")).status("ACTIVE")
                    .userId(u1.getUserId()).createdAt(LocalDateTime.now()).build());

            accountRepository.save(Account.builder()
                    .accountNumber("ACC-20002001").accountType("SAVINGS")
                    .balance(new BigDecimal("8750.00")).status("ACTIVE")
                    .userId(u2.getUserId()).createdAt(LocalDateTime.now()).build());

            accountRepository.save(Account.builder()
                    .accountNumber("ACC-20003001").accountType("PREMIUM")
                    .balance(new BigDecimal("55000.00")).status("ACTIVE")
                    .userId(u3.getUserId()).createdAt(LocalDateTime.now()).build());

            accountRepository.save(Account.builder()
                    .accountNumber("ACC-20004001").accountType("CHECKING")
                    .balance(new BigDecimal("150.00")).status("FROZEN")
                    .userId(u4.getUserId()).createdAt(LocalDateTime.now()).build());

            accountRepository.save(Account.builder()
                    .accountNumber("ACC-20005001").accountType("SAVINGS")
                    .balance(new BigDecimal("22000.00")).status("ACTIVE")
                    .userId(u5.getUserId()).createdAt(LocalDateTime.now()).build());

            log.info("Sample data loaded: 5 users, 6 accounts.");
        }
    }
}
