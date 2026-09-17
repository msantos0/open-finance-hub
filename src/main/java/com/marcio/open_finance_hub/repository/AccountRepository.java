package com.marcio.open_finance_hub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.marcio.open_finance_hub.model.Account;

public interface AccountRepository extends MongoRepository<Account, String> {

    List<Account> findByUserId(String userId);

    List<Account> findByUserIdAndActiveTrue(String userId);

    Optional<Account> findByIdAndUserId(String id, String userId);

    boolean existsByIdAndUserId(String id, String userId);
}