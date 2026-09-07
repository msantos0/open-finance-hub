package com.marcio.open_finance_hub.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(String id) {
        super("Category not found: " + id);
    }
}