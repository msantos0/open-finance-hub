package com.marcio.open_finance_hub.dto;

import java.math.BigDecimal;

public record MonthlyEvolutionResponseDTO(
        String month,
        BigDecimal income,
        BigDecimal expense) {
}