package com.marcio.open_finance_hub.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.marcio.open_finance_hub.model.InvestmentType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InvestmentRequestDTO(
        @NotNull(message = "Investment type is required")
        InvestmentType type,

        @NotBlank(message = "Institution is required")
        @Size(max = 150, message = "Institution must have at most 150 characters")
        String institution,

        @NotBlank(message = "Description is required")
        @Size(max = 500, message = "Description must have at most 500 characters")
        String description,

        @NotNull(message = "Invested amount is required")
        @DecimalMin(value = "0.00", inclusive = false, message = "Invested amount must be positive")
        BigDecimal investedAmount,

        @NotNull(message = "Current value is required")
        @DecimalMin(value = "0.00", message = "Current value must be non-negative")
        BigDecimal currentValue,

        @NotNull(message = "Annual rate is required")
        @DecimalMin(value = "0.00", message = "Annual rate must be non-negative")
        BigDecimal annualRate,

        @NotNull(message = "Application date is required")
        LocalDate applicationDate,

        LocalDate maturityDate) {
}