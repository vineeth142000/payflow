package com.payflow.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @ExtendWith(MockitoExtension.class)
 * Tells JUnit to use Mockito for this test class.
 * Mockito creates fake versions of your dependencies
 * so you can test AccountService in complete isolation
 * without needing a real database or real repository.
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    /**
     * @Mock = create a fake AccountRepository.
     * This fake repository doesn't talk to any database.
     * We control exactly what it returns in each test.
     *
     * Real world analogy: instead of using a real bank vault,
     * we use a prop vault for the test. We control what's inside.
     */
    @Mock
    private AccountRepository accountRepository;

    /**
     * @InjectMocks = create a real AccountService
     * and inject the fake repository into it.
     *
     * So AccountService thinks it has a real repository
     * but actually it has our fake one that we control.
     */
    @InjectMocks
    private AccountService accountService;

    /**
     * TEST 1 — Happy path: create account successfully
     *
     * Scenario: Vineeth sends a valid request to create an account.
     * Email doesn't exist yet. Everything should work perfectly.
     */
    @Test
    @DisplayName("Should create account successfully when all inputs are valid")
    void shouldCreateAccountSuccessfully() {

        // ARRANGE — set up the test scenario
        CreateAccountRequest request = new CreateAccountRequest();
        request.setOwnerName("Vineeth Varma");
        request.setEmail("vineeth@payflow.com");
        request.setInitialBalance(BigDecimal.valueOf(1000));

        // Tell the fake repository:
        // "When someone asks if this email exists, say NO"
        when(accountRepository.existsByEmail("vineeth@payflow.com"))
                .thenReturn(false);

        // Tell the fake repository:
        // "When someone calls save(), return the same account back"
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> {
                    // get the account that was passed to save()
                    Account account = invocation.getArgument(0);
                    // simulate database setting the ID
                    account.setId(1L);
                    account.setStatus(AccountStatus.ACTIVE);
                    account.setCreatedAt(LocalDateTime.now());
                    return account;
                });

        // ACT — call the actual method we're testing
        AccountResponse response = accountService.createAccount(request);

        // ASSERT — verify the results are correct
        assertThat(response).isNotNull();
        assertThat(response.getOwnerName()).isEqualTo("Vineeth Varma");
        assertThat(response.getEmail()).isEqualTo("vineeth@payflow.com");
        assertThat(response.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(response.getAccountNumber()).startsWith("ACC-");
        assertThat(response.getStatus()).isEqualTo("ACTIVE");

        // verify that save() was actually called exactly once
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    /**
     * TEST 2 — Duplicate email: should throw exception
     *
     * Scenario: Vineeth tries to create a second account
     * with the same email. System should reject it.
     */
    @Test
    @DisplayName("Should throw DuplicateAccountException when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        // ARRANGE
        CreateAccountRequest request = new CreateAccountRequest();
        request.setOwnerName("Vineeth Varma");
        request.setEmail("vineeth@payflow.com");
        request.setInitialBalance(BigDecimal.valueOf(1000));

        // Tell the fake repository:
        // "When someone asks if this email exists, say YES"
        when(accountRepository.existsByEmail("vineeth@payflow.com"))
                .thenReturn(true);

        // ACT + ASSERT — verify the exception is thrown
        assertThatThrownBy(() -> accountService.createAccount(request))
                .isInstanceOf(DuplicateAccountException.class)
                .hasMessageContaining("vineeth@payflow.com");

        // verify that save() was NEVER called
        // because we should reject before saving
        verify(accountRepository, never()).save(any(Account.class));
    }

    /**
     * TEST 3 — Get account by ID: success
     *
     * Scenario: Someone requests account details with a valid ID.
     */
    @Test
    @DisplayName("Should return account when valid ID is provided")
    void shouldReturnAccountWhenValidIdProvided() {

        // ARRANGE — build a fake account to return from repository
        Account account = Account.builder()
                .id(1L)
                .accountNumber("ACC-DE4A9137")
                .ownerName("Vineeth Varma")
                .email("vineeth@payflow.com")
                .balance(BigDecimal.valueOf(1000))
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        // Tell the fake repository:
        // "When someone asks for account with ID 1, return this account"
        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        // ACT
        AccountResponse response = accountService.getAccount(1L);

        // ASSERT
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getAccountNumber()).isEqualTo("ACC-DE4A9137");
        assertThat(response.getOwnerName()).isEqualTo("Vineeth Varma");
        assertThat(response.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    /**
     * TEST 4 — Get account by ID: not found
     *
     * Scenario: Someone requests an account that doesn't exist.
     * System should throw AccountNotFoundException.
     */
    @Test
    @DisplayName("Should throw AccountNotFoundException when account ID does not exist")
    void shouldThrowExceptionWhenAccountNotFound() {

        // ARRANGE
        // Tell the fake repository:
        // "When someone asks for account with ID 999, return empty"
        when(accountRepository.findById(999L))
                .thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThatThrownBy(() -> accountService.getAccount(999L))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("999");

        // verify findById was called with the right ID
        verify(accountRepository).findById(999L);
    }

    /**
     * TEST 5 — Get account by account number: success
     */
    @Test
    @DisplayName("Should return account when valid account number is provided")
    void shouldReturnAccountWhenValidAccountNumberProvided() {

        // ARRANGE
        Account account = Account.builder()
                .id(1L)
                .accountNumber("ACC-DE4A9137")
                .ownerName("Vineeth Varma")
                .email("vineeth@payflow.com")
                .balance(BigDecimal.valueOf(1000))
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        when(accountRepository.findByAccountNumber("ACC-DE4A9137"))
                .thenReturn(Optional.of(account));

        // ACT
        AccountResponse response = accountService.getAccountByNumber("ACC-DE4A9137");

        // ASSERT
        assertThat(response).isNotNull();
        assertThat(response.getAccountNumber()).isEqualTo("ACC-DE4A9137");
        assertThat(response.getOwnerName()).isEqualTo("Vineeth Varma");
    }

    /**
     * TEST 6 — Get account by account number: not found
     */
    @Test
    @DisplayName("Should throw AccountNotFoundException when account number does not exist")
    void shouldThrowExceptionWhenAccountNumberNotFound() {

        // ARRANGE
        when(accountRepository.findByAccountNumber("ACC-INVALID"))
                .thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThatThrownBy(() -> accountService.getAccountByNumber("ACC-INVALID"))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("ACC-INVALID");
    }
}