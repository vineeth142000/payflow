package com.payflow.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for publishing events to Kafka.
 *
 * KafkaTemplate is Spring's helper class for sending messages.
 * Think of it like a mailman — you give it a message and a topic,
 * and it delivers it to Kafka.
 *
 * @Component = Spring manages this as a bean
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventProducer {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    // this is the topic name — like a WhatsApp group name
    private static final String TOPIC = "transaction.created";

    public void publishTransactionEvent(TransactionEvent event) {
        log.info("Publishing transaction event to Kafka: {}", event.getTransactionId());

        /**
         * kafkaTemplate.send(topic, key, value)
         * topic = which Kafka topic to publish to
         * key   = transactionId — Kafka uses this to decide which partition
         *         messages with the same key always go to the same partition
         *         this ensures ordering for the same transaction
         * value = the actual event object — gets serialized to JSON
         */
        kafkaTemplate.send(TOPIC, event.getTransactionId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Event published successfully for transaction: {} to partition: {}",
                                event.getTransactionId(),
                                result.getRecordMetadata().partition());
                    } else {
                        log.error("Failed to publish event for transaction: {}",
                                event.getTransactionId(), ex);
                    }
                });
    }
}