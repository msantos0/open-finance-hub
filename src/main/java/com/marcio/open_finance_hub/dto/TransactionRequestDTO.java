package com.marcio.open_finance_hub.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.marcio.open_finance_hub.model.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TransactionRequestDTO(
        @NotBlank(message = "Description is required")
        @Size(max = 255, message = "Description must have at most 255 characters")
        String description,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "Transaction type is required")
        TransactionType transactionType,

        @NotNull(message = "Transaction date is required")
        LocalDate transactionDate,

        @NotBlank(message = "Category id is required")
        String categoryId,

        @NotBlank(message = "Account id is required")
        String accountId) {
}