package com.payflow.account;

/**
 * Thrown when an account cannot be found in the database.
 * Using a specific exception class instead of RuntimeException
 * lets us handle each error type differently in the exception handler.
 *
 * extends RuntimeException = unchecked exception, no need to
 * declare it in method signatures with "throws"
 */

public class AccountNotFoundException extends RuntimeException
{
    public AccountNotFoundException(String message){
        super(message);
    }
}

