package com.marcio.open_finance_hub.exception;

public class InvestmentNotFoundException extends RuntimeException {

    public InvestmentNotFoundException(String id) {
        super("Investment not found: " + id);
    }
}