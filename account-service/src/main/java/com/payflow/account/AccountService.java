package com.payflow.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {

        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateAccountException(
                    "Account with email " + request.getEmail() + " already exists"
            );
        }

        String accountNumber = "ACC-" + UUID.randomUUID()
                .toString().substring(0, 8).toUpperCase();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .ownerName(request.getOwnerName())
                .email(request.getEmail())
                .balance(request.getInitialBalance())
                .build();

        Account savedAccount = accountRepository.save(account);

        log.info("Created account {} for {}",
                savedAccount.getAccountNumber(),
                savedAccount.getOwnerName());

        return mapToResponse(savedAccount);
    }
    /**
     * @Cacheable("accounts") — check Redis first.
     *before hitting PostgreSQL, check Redis first.
     *
     * Cache key = "accounts::1" (Cache name + "::" +id)
     *
     * First call:
     *   → Redis miss
     *   → hits PostgreSQL
     *   → stores result in Redis
     *   → returns result
     *
     * Second call:
     *   → Redis hit
     *   → returns immediately
     *   → PostgreSQL NEVER touched
     *   → you'll see NO Hibernate SQL in console:
     *    First call: "Hibernate: select * from accounts where id=?"
     *    Second call: (no Hibernate log — served from Redis)
     */

    @Cacheable(value = "accounts ", key = "#id")
    public AccountResponse getAccount(Long id) {
        log.info("Cache miss - fetching account {} from database" , id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(
                        "No account found with id: " + id
                ));
        return mapToResponse(account);
    }
    /**
     * @Cacheable with account number as key.
     * Cache key = "accounts::ACC-DE4A9137"
     */
    @Cacheable(value = "accounts ", key = "#accountNumber")
    public AccountResponse getAccountByNumber(String accountNumber) {
        log.info("Cache miss - fetching account {} from database", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(
                        "No account found with number: " + accountNumber
                ));
        return mapToResponse(account);
    }
     /**
     * @CacheEvict — when balance updates, clear the cache.
     * We evict BOTH cache entries for this account
     * (by ID and by account number) so stale data
     * never gets served.
     *
     * Next request after eviction → cache miss → fresh data from DB(PostgreSQL)
     */
    @Caching(evict = {
            @CacheEvict(value = "accounts",key = "#id"),
            @CacheEvict(value = "accounts",key = "#accountNumber")
    })
    public void evictAccountCache(Long id, String accountNumber){
        log.info("Cache evicted for account {} / {}" , id, accountNumber);
    }
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