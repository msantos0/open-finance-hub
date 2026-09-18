package com.marcio.open_finance_hub.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.marcio.open_finance_hub.dto.CategoryExpenseResponseDTO;
import com.marcio.open_finance_hub.dto.DashboardSummaryResponseDTO;
import com.marcio.open_finance_hub.dto.MonthlyEvolutionResponseDTO;
import com.marcio.open_finance_hub.dto.TransactionResponseDTO;
import com.marcio.open_finance_hub.model.Transaction;
import com.marcio.open_finance_hub.model.TransactionType;
import com.marcio.open_finance_hub.repository.CategoryRepository;
import com.marcio.open_finance_hub.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final CurrentUserService currentUserService;

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

        for (var transaction : transactionRepository.findByUserId(currentUserService.get().getId())) {
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

    public List<CategoryExpenseResponseDTO> getCategoryExpenses() {
        String userId = currentUserService.get().getId();
        Map<String, String> categoryNames = categoryRepository.findByUserId(userId).stream()
                .collect(java.util.stream.Collectors.toMap(category -> category.getId(), category -> category.getName()));
        Map<String, BigDecimal> expensesByCategory = new HashMap<>();

        for (Transaction transaction : transactionRepository.findByUserId(userId)) {
            if (transaction.getTransactionType() != TransactionType.EXPENSE) {
                continue;
            }
            expensesByCategory.merge(transaction.getCategoryId(), transaction.getAmount(), BigDecimal::add);
        }

        return expensesByCategory.entrySet().stream()
                .map(entry -> new CategoryExpenseResponseDTO(
                        categoryNames.getOrDefault(entry.getKey(), "Uncategorized"), entry.getValue()))
                .sorted(Comparator.comparing(CategoryExpenseResponseDTO::amount).reversed())
                .toList();
    }

    public List<MonthlyEvolutionResponseDTO> getMonthlyEvolution() {
        String userId = currentUserService.get().getId();
        YearMonth firstMonth = YearMonth.now().minusMonths(11);
        Map<YearMonth, BigDecimal[]> totalsByMonth = new LinkedHashMap<>();
        for (int index = 0; index < 12; index++) {
            totalsByMonth.put(firstMonth.plusMonths(index), new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO });
        }

        for (Transaction transaction : transactionRepository.findByUserId(userId)) {
            YearMonth month = YearMonth.from(transaction.getTransactionDate());
            BigDecimal[] totals = totalsByMonth.get(month);
            if (totals == null) {
                continue;
            }
            if (transaction.getTransactionType() == TransactionType.INCOME) {
                totals[0] = totals[0].add(transaction.getAmount());
            } else if (transaction.getTransactionType() == TransactionType.EXPENSE) {
                totals[1] = totals[1].add(transaction.getAmount());
            }
        }

        return totalsByMonth.entrySet().stream()
                .map(entry -> new MonthlyEvolutionResponseDTO(
                        entry.getKey().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                        entry.getValue()[0],
                        entry.getValue()[1]))
                .toList();
    }

    public List<TransactionResponseDTO> getRecentTransactions() {
        return transactionRepository.findByUserId(currentUserService.get().getId()).stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                .limit(10)
                .map(this::toTransactionResponse)
                .toList();
    }

    private TransactionResponseDTO toTransactionResponse(Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getTransactionDate(),
                transaction.getCategoryId(),
                transaction.getAccountId(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt());
    }
}