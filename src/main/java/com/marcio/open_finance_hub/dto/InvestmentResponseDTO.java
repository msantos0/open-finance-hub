package com.marcio.open_finance_hub.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.marcio.open_finance_hub.model.InvestmentType;

public record InvestmentResponseDTO(
        String id,
        InvestmentType type,
        String institution,
        String description,
        BigDecimal investedAmount,
        BigDecimal currentValue,
        BigDecimal annualRate,
        LocalDate applicationDate,
        LocalDate maturityDate,
        Instant createdAt,
        Instant updatedAt) {
}