package com.marcio.open_finance_hub.dto;

import java.math.BigDecimal;

public record CategoryExpenseResponseDTO(
        String category,
        BigDecimal amount) {
}