package com.marcio.open_finance_hub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.marcio.open_finance_hub.model.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {

	List<Category> findByUserId(String userId);

	Optional<Category> findByIdAndUserId(String id, String userId);

	boolean existsByIdAndUserId(String id, String userId);
}