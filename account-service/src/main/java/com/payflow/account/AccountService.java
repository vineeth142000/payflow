package com.payflow.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

/**
 * @Service = tells Spring this is a service bean — Spring manages its lifecycle
 * @RequiredArgsConstructor = Lombok generates constructor injection automatically
 * @Slf4j = gives you a log object for free — use log.info(), log.error() etc
 * @Transactional = if anything fails inside the method, ALL database changes are rolled back
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {

        // Check if email already exists — no duplicate accounts
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Account with email " + request.getEmail() + " already exists");
        }

        // Generate a unique account number
        String accountNumber = "ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Build the Account entity using the Builder pattern
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .ownerName(request.getOwnerName())
                .email(request.getEmail())
                .balance(request.getInitialBalance())
                .build();

        // Save to PostgreSQL — one line, no SQL written
        Account savedAccount = accountRepository.save(account);

        log.info("Created account {} for {}", savedAccount.getAccountNumber(), savedAccount.getOwnerName());

        // Convert entity to response DTO and return
        return mapToResponse(savedAccount);
    }

    public AccountResponse getAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
        return mapToResponse(account);
    }

    public AccountResponse getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
        return mapToResponse(account);
    }

    // Private helper — converts Account entity to AccountResponse DTO
    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .ownerName(account.getOwnerName())
                .email(account.getEmail())
                .balance(account.getBalance())
                .status(account.getStatus().name())
                .createdAt(account.getCreatedAt())
                .build();
    }
}