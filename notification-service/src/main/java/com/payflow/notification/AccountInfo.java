package com.payflow.notification;

import lombok.Data;

/**
 * This maps the JSON response from Account Service.
 * We only need ownerName — we don't need to map every field.
 * Jackson (JSON library) ignores unknown fields by default.
 */
@Data
public class AccountInfo {
    private String accountNumber;
    private String ownerName;
    private String email;
}