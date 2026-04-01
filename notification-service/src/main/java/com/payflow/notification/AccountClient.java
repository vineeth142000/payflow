package com.payflow.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * This class talks to Account Service over HTTP.
 * Notification Service calls Account Service's REST API
 * to look up who owns a given account number.
 *
 * This is service-to-service communication.
 * In production this would use service discovery (Eureka/Kubernetes DNS)
 * For now we hardcode the URL since we know it's on port 8081
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AccountClient {

    private final RestTemplate restTemplate;

    private static final String ACCOUNT_SERVICE_URL = "http://localhost:8081/api/accounts/number/";

    public String getOwnerName(String accountNumber) {
        try {
            /**
             * getForObject = HTTP GET and deserialize response into a class
             * We call: GET http://localhost:8081/api/accounts/number/ACC-DE4A9137
             * Account Service returns AccountResponse JSON
             * We map it to AccountInfo class below
             */
            AccountInfo account = restTemplate.getForObject(
                    ACCOUNT_SERVICE_URL + accountNumber,
                    AccountInfo.class
            );

            if (account != null) {
                return account.getOwnerName();
            }
        } catch (Exception e) {
            log.warn("Could not fetch owner name for account {}: {}", accountNumber, e.getMessage());
        }

        // fallback — if Account Service is down, use account number
        return accountNumber;
    }
}