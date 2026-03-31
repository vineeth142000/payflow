package com.payflow.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionEventProducer eventProducer;

    @Transactional
    public TransactionResponse transfer(TransferRequest request) {

        // generate unique transaction ID — like a receipt number
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        log.info("Processing transfer {} — {} sending {} to {}",
                transactionId,
                request.getFromAccount(),
                request.getAmount(),
                request.getToAccount());

        // Step 1 — save the transaction record to our database
        Transaction transaction = Transaction.builder()
                .transactionId(transactionId)
                .fromAccount(request.getFromAccount())
                .toAccount(request.getToAccount())
                .amount(request.getAmount())
                .type(TransactionType.TRANSFER)
                .description(request.getDescription())
                .build();

        Transaction saved = transactionRepository.save(transaction);

        // Step 2 — publish event to Kafka
        // This is the KEY moment — we tell the world "a transaction happened"
        // Account Service will pick this up and update the balances
        TransactionEvent event = TransactionEvent.builder()
                .transactionId(transactionId)
                .fromAccount(request.getFromAccount())
                .toAccount(request.getToAccount())
                .amount(request.getAmount())
                .type(TransactionType.TRANSFER.name())
                .timestamp(LocalDateTime.now())
                .build();

        eventProducer.publishTransactionEvent(event);

        log.info("Transfer {} completed and event published to Kafka", transactionId);

        return mapToResponse(saved);
    }

    public TransactionResponse getTransaction(String transactionId) {
        Transaction transaction = transactionRepository
                .findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));
        return mapToResponse(transaction);
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionId(transaction.getTransactionId())
                .fromAccount(transaction.getFromAccount())
                .toAccount(transaction.getToAccount())
                .amount(transaction.getAmount())
                .type(transaction.getType().name())
                .status(transaction.getStatus().name())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}