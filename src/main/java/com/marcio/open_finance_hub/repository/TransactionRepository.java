package com.marcio.open_finance_hub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.marcio.open_finance_hub.model.Transaction;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

	List<Transaction> findByUserId(String userId);

	Optional<Transaction> findByIdAndUserId(String id, String userId);
}