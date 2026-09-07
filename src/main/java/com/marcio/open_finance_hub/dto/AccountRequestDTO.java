package com.marcio.open_finance_hub.dto;

import java.math.BigDecimal;

import com.marcio.open_finance_hub.model.AccountType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AccountRequestDTO(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must have at most 100 characters")
        String name,

        @NotBlank(message = "Bank is required")
        @Size(max = 100, message = "Bank must have at most 100 characters")
        String bank,

        @NotNull(message = "Account type is required")
        AccountType accountType,

        @NotNull(message = "Initial balance is required")
        @DecimalMin(value = "0.00", message = "Initial balance must be non-negative")
        BigDecimal initialBalance,

        Boolean active) {
}