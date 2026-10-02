package com.marcio.open_finance_hub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.marcio.open_finance_hub.model.Investment;

public interface InvestmentRepository extends MongoRepository<Investment, String> {

    List<Investment> findByUserId(String userId);

    Optional<Investment> findByIdAndUserId(String id, String userId);
}