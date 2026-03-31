package com.payflow.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * This is the message that gets sent to Kafka.
 *
 * It must be serializable to JSON — Kafka sends it as a JSON string
 * over the network. The consumer (Account Service) will receive
 * this JSON and deserialize it back into an object.
 *
 * @NoArgsConstructor is required for JSON deserialization to work.
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