package com.marcio.open_finance_hub.dto;

import com.marcio.open_finance_hub.model.CategoryType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must have at most 100 characters")
        String name,

        @Size(max = 500, message = "Description must have at most 500 characters")
        String description,

        @NotNull(message = "Type is required")
        CategoryType type) {
}