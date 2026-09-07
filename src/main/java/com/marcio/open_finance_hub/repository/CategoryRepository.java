package com.marcio.open_finance_hub.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.marcio.open_finance_hub.model.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {
}