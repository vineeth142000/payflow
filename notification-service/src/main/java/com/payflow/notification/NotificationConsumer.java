package com.payflow.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final AccountClient accountClient;

    @KafkaListener(
            topics = "transaction.created",
            groupId = "notification-group"
    )
    public void handleTransactionEvent(TransactionEvent event) {

        // look up real names from Account Service
        String senderName = accountClient.getOwnerName(event.getFromAccount());
        String receiverName = accountClient.getOwnerName(event.getToAccount());

        log.info("---------------------------------------------");
        log.info("New transaction event received!");
        log.info("Transaction ID : {}", event.getTransactionId());
        log.info("From           : {} ({})", senderName, event.getFromAccount());
        log.info("To             : {} ({})", receiverName, event.getToAccount());
        log.info("Amount         : ${}", event.getAmount());
        log.info("---------------------------------------------");

        sendTransferNotification(event, senderName, receiverName);
    }

    private void sendTransferNotification(TransactionEvent event,
                                          String senderName,
                                          String receiverName) {

        log.info("SMS to {} : You sent ${} to {}. Ref: {}",
                senderName,
                event.getAmount(),
                receiverName,
                event.getTransactionId());

        log.info("SMS to {} : You received ${} from {}. Ref: {}",
                receiverName,
                event.getAmount(),
                senderName,
                event.getTransactionId());
    }
}