package com.marcio.open_finance_hub.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.marcio.open_finance_hub.model.TransactionType;

public record TransactionResponseDTO(
        String id,
        String description,
        BigDecimal amount,
        TransactionType transactionType,
        LocalDate transactionDate,
        String categoryId,
        String accountId,
        Instant createdAt,
        Instant updatedAt) {
}