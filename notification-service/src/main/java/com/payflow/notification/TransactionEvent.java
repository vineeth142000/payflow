package com.payflow.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * This mirrors the TransactionEvent in transaction-service.
 * Kafka sends JSON over the wire — this class is used to
 * deserialize that JSON back into a Java object.
 *
 * Both sides must have matching field names — that's the contract.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    private String transactionId;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private String type;
    private LocalDateTime timestamp;
}