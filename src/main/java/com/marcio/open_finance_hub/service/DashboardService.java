package com.marcio.open_finance_hub.service;

import java.math.BigDecimal;

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
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        long transactionCount = 0;

        for (var transaction : transactionRepository.findAll()) {
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