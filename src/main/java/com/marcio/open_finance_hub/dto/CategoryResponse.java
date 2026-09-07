package com.marcio.open_finance_hub.dto;

import java.time.Instant;

import com.marcio.open_finance_hub.model.CategoryType;

public record CategoryResponse(
        String id,
        String name,
        String description,
        CategoryType type,
        Instant createdAt,
        Instant updatedAt) {
}