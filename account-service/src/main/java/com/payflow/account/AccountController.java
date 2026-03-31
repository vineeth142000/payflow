package com.payflow.account;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @RestController = this class handles HTTP requests and returns JSON responses
 * @RequestMapping = all endpoints in this class start with /api/accounts
 * @RequiredArgsConstructor = auto injects AccountService via constructor
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    /**
     * POST /api/accounts
     * Creates a new bank account
     *
     * @Valid = triggers validation on CreateAccountRequest
     * If validation fails, Spring automatically returns 400 Bad Request
     * with details of what's wrong — before your code even runs
     *
     * ResponseEntity = lets you control the HTTP status code
     * 201 CREATED = standard HTTP response for successful resource creation
     */
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {
        log.info("Request to create account for {}", request.getEmail());
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/accounts/{id}
     * Gets account by ID
     *
     * @PathVariable = extracts {id} from the URL
     * Example: GET /api/accounts/1 → id = 1
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccount(id));
    }

    /**
     * GET /api/accounts/number/{accountNumber}
     * Gets account by account number like ACC-ABC12345
     */
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountByNumber(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccountByNumber(accountNumber));
    }
}