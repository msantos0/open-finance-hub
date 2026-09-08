package com.marcio.open_finance_hub.dto;

import java.math.BigDecimal;

public record DashboardSummaryResponseDTO(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal currentBalance,
        long transactionCount) {
}