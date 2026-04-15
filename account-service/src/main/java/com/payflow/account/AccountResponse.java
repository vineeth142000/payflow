package com.payflow.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * This is what we send BACK to the caller.
 * Notice we don't include sensitive internal fields.
 * The caller gets only what they need to know.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private String ownerName;
    private String email;
    private BigDecimal balance;
    private String status;
    private LocalDateTime createdAt;
}