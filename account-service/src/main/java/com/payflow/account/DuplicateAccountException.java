package com.payflow.account;

public class DuplicateAccountException extends RuntimeException
{
    public DuplicateAccountException(String message){
        super(message);
    }
}
