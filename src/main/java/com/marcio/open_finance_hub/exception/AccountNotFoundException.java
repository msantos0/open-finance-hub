package com.marcio.open_finance_hub.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String id) {
        super("Account not found: " + id);
    }
}