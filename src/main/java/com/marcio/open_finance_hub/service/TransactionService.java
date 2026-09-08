package com.marcio.open_finance_hub.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.marcio.open_finance_hub.dto.TransactionRequestDTO;
import com.marcio.open_finance_hub.dto.TransactionResponseDTO;
import com.marcio.open_finance_hub.exception.AccountNotFoundException;
import com.marcio.open_finance_hub.exception.CategoryNotFoundException;
import com.marcio.open_finance_hub.exception.TransactionNotFoundException;
import com.marcio.open_finance_hub.model.Transaction;
import com.marcio.open_finance_hub.repository.AccountRepository;
import com.marcio.open_finance_hub.repository.CategoryRepository;
import com.marcio.open_finance_hub.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    public TransactionResponseDTO create(TransactionRequestDTO request) {
        validateReferences(request.categoryId(), request.accountId());
        Instant now = Instant.now();
        Transaction transaction = Transaction.builder()
                .description(request.description().trim())
                .amount(request.amount())
                .transactionType(request.transactionType())
                .transactionDate(request.transactionDate())
                .categoryId(request.categoryId().trim())
                .accountId(request.accountId().trim())
                .createdAt(now)
                .updatedAt(now)
                .build();

        return toResponse(transactionRepository.save(transaction));
    }

    public List<TransactionResponseDTO> findAll() {
        return transactionRepository.findAll().stream().map(this::toResponse).toList();
    }

    public TransactionResponseDTO findById(String id) {
        return toResponse(findTransaction(id));
    }

    public TransactionResponseDTO update(String id, TransactionRequestDTO request) {
        Transaction transaction = findTransaction(id);
        validateReferences(request.categoryId(), request.accountId());
        transaction.setDescription(request.description().trim());
        transaction.setAmount(request.amount());
        transaction.setTransactionType(request.transactionType());
        transaction.setTransactionDate(request.transactionDate());
        transaction.setCategoryId(request.categoryId().trim());
        transaction.setAccountId(request.accountId().trim());
        transaction.setUpdatedAt(Instant.now());

        return toResponse(transactionRepository.save(transaction));
    }

    public void delete(String id) {
        Transaction transaction = findTransaction(id);
        transactionRepository.delete(transaction);
    }

    private Transaction findTransaction(String id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    private void validateReferences(String categoryId, String accountId) {
        if (!categoryRepository.existsById(categoryId.trim())) {
            throw new CategoryNotFoundException(categoryId);
        }
        if (!accountRepository.existsById(accountId.trim())) {
            throw new AccountNotFoundException(accountId);
        }
    }

    private TransactionResponseDTO toResponse(Transaction transaction) {
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