package com.payflow.account;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Entity tells Spring/JPA: "this class is a database table"
 * @Table tells it the table name in PostgreSQL
 *
 * Lombok annotations:
 * @Data        = generates getters, setters, toString, equals, hashCode automatically
 * @Builder     = lets you create objects like: Account.builder().name("Vineeth").build()
 * @NoArgsConstructor = generates empty constructor Account()
 * @AllArgsConstructor = generates constructor with all fields
 */
@Entity
@Table(name = "accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    /**
     * @Id = this is the primary key (unique identifier for each row)
     * @GeneratedValue = database auto-generates this value, you don't set it manually
     * IDENTITY strategy = uses PostgreSQL's auto-increment (1, 2, 3, 4...)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Column = maps this field to a column in the database
     * nullable = false means this column cannot be empty
     * unique = true means no two accounts can have the same account number
     */
    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * BigDecimal is used for money — never use double or float for money
     * because floating point math has rounding errors.
     * Example: 0.1 + 0.2 = 0.30000000000000004 in double
     * BigDecimal gives you exact precision — critical for banking
     */
    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    /**
     * Enum stored as a String in the database
     * So instead of storing 0 or 1, it stores "ACTIVE" or "INACTIVE"
     * Much more readable
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status;

    /**
     * @Column(updatable = false) means once this is set, it never changes
     * Perfect for "created at" timestamps
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * @PrePersist = runs automatically BEFORE the entity is saved for the first time
     * So you never forget to set createdAt — it happens automatically
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status = AccountStatus.ACTIVE;
    }

    /**
     * @PreUpdate = runs automatically BEFORE the entity is updated
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}