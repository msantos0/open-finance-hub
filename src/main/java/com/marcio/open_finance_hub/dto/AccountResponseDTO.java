package com.marcio.open_finance_hub.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.marcio.open_finance_hub.model.AccountType;

public record AccountResponseDTO(
        String id,
        String name,
        String bank,
        AccountType accountType,
        BigDecimal initialBalance,
        BigDecimal currentBalance,
        boolean active,
        Instant createdAt,
        Instant updatedAt) {
}