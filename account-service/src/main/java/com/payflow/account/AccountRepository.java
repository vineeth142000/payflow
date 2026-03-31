package com.payflow.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * JpaRepository gives you database operations FOR FREE.
 * You don't write any SQL. Spring generates it automatically.
 *
 * JpaRepository<Account, Long> means:
 * - Account = the entity/table we're working with
 * - Long = the type of the primary key (our id field)
 *
 * Just by extending JpaRepository you get:
 * - save(account)          → INSERT or UPDATE
 * - findById(id)           → SELECT WHERE id = ?
 * - findAll()              → SELECT * FROM accounts
 * - deleteById(id)         → DELETE WHERE id = ?
 * - count()                → SELECT COUNT(*)
 * ...and many more. All for free, zero SQL written.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Spring reads the method name and generates the SQL automatically.
     * findByEmail → SELECT * FROM accounts WHERE email = ?
     * This is called "derived query" — Spring derives SQL from method names.
     */
    Optional<Account> findByEmail(String email);

    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByEmail(String email);
}