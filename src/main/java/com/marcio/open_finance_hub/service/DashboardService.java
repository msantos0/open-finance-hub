package com.marcio.open_finance_hub.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.stereotype.Service;

import com.marcio.open_finance_hub.dto.DashboardSummaryResponseDTO;
import com.marcio.open_finance_hub.model.TransactionType;
import com.marcio.open_finance_hub.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;

    public DashboardSummaryResponseDTO getSummary() {
        return getSummary(null, null);
    }

    public DashboardSummaryResponseDTO getSummary(Integer month, Integer year) {
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        long transactionCount = 0;
        LocalDate startDate = null;
        LocalDate endDate = null;

        if (month != null && year != null) {
            YearMonth period = YearMonth.of(year, month);
            startDate = period.atDay(1);
            endDate = period.atEndOfMonth();
        }

        for (var transaction : transactionRepository.findAll()) {
            if (startDate != null && (transaction.getTransactionDate().isBefore(startDate)
                    || transaction.getTransactionDate().isAfter(endDate))) {
                continue;
            }
            transactionCount++;
            if (transaction.getTransactionType() == TransactionType.INCOME) {
                totalIncome = totalIncome.add(transaction.getAmount());
            } else if (transaction.getTransactionType() == TransactionType.EXPENSE) {
                totalExpense = totalExpense.add(transaction.getAmount());
            }
        }

        return new DashboardSummaryResponseDTO(
                totalIncome,
                totalExpense,
                totalIncome.subtract(totalExpense),
                transactionCount);
    }
}