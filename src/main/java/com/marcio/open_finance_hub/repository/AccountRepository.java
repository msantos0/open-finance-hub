package com.marcio.open_finance_hub.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.marcio.open_finance_hub.model.Account;

public interface AccountRepository extends MongoRepository<Account, String> {

    List<Account> findByActiveTrue();
}