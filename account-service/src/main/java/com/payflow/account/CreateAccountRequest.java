package com.payflow.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

/**
 * This is what the API caller must send in the request body.
 * Validation annotations ensure bad data never reaches your business logic.
 *
 * @NotBlank = field cannot be null or empty string
 * @Email     = must be a valid email format
 * @NotNull   = cannot be null
 * @Positive  = must be a positive number (> 0)
 */
@Data
public class CreateAccountRequest {

    @NotBlank(message = "Owner name is required")
    private String ownerName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "Initial balance is required")
    @Positive(message = "Initial balance must be positive")
    private BigDecimal initialBalance;
}