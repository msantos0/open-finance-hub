package com.marcio.open_finance_hub.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.marcio.open_finance_hub.dto.CategoryRequest;
import com.marcio.open_finance_hub.dto.CategoryResponse;
import com.marcio.open_finance_hub.exception.CategoryNotFoundException;
import com.marcio.open_finance_hub.model.Category;
import com.marcio.open_finance_hub.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse create(CategoryRequest request) {
        Instant now = Instant.now();
        Category category = Category.builder()
                .name(request.name().trim())
                .description(normalizeDescription(request.description()))
                .type(request.type())
                .createdAt(now)
                .updatedAt(now)
                .build();

        return toResponse(categoryRepository.save(category));
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CategoryResponse findById(String id) {
        return toResponse(findCategory(id));
    }

    public CategoryResponse update(String id, CategoryRequest request) {
        Category category = findCategory(id);
        category.setName(request.name().trim());
        category.setDescription(normalizeDescription(request.description()));
        category.setType(request.type());
        category.setUpdatedAt(Instant.now());

        return toResponse(categoryRepository.save(category));
    }

    public void delete(String id) {
        Category category = findCategory(id);
        categoryRepository.delete(category);
    }

    private Category findCategory(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getType(),
                category.getCreatedAt(),
                category.getUpdatedAt());
    }

    private String normalizeDescription(String description) {
        return description == null || description.isBlank() ? null : description.trim();
    }
}