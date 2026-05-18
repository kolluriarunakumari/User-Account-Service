package com.microservice.service;

import com.microservice.model.Account;
import com.microservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public List<Account> getAccountsByUserId(String userId) {
        return accountRepository.findByUserId(userId);
    }

    public Account createAccount(Account account) {
        if (accountRepository.existsByAccountNumber(account.getAccountNumber())) {
            throw new IllegalArgumentException("Account number already exists: " + account.getAccountNumber());
        }
        return accountRepository.save(account);
    }

    public Optional<Account> updateAccount(String accountNumber, Account updatedAccount) {
        return accountRepository.findByAccountNumber(accountNumber).map(existing -> {
            existing.setAccountType(updatedAccount.getAccountType());
            existing.setBalance(updatedAccount.getBalance());
            existing.setStatus(updatedAccount.getStatus());
            return accountRepository.save(existing);
        });
    }

    public boolean deleteAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).map(account -> {
            accountRepository.delete(account);
            return true;
        }).orElse(false);
    }
}
