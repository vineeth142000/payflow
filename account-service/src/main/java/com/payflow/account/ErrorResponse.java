package com.payflow.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * This is the standard error shape returned by ALL error responses.
 * Every API error across the entire platform looks exactly the same.
 * Consistent error responses are a sign of a mature API.
 *
 * @JsonInclude(NON_NULL) = fields that are null won't appear in JSON
 * So if there are no validation errors, the "errors" field won't show up
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;

    /**
     * This field only appears for validation errors.
     * Example: { "ownerName": "must not be blank", "email": "must be valid" }
     */
    private Map<String, String> errors;
}