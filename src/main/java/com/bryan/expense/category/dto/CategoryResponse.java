package com.bryan.expense.category.dto;

import com.bryan.expense.category.Category;

import java.time.LocalDateTime;

public record CategoryResponse(
        String id,
        String name,
        String type,
        String icon,
        String color,
        LocalDateTime createdAt
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId() != null ? category.getId().toHexString() : null,
                category.getName(),
                category.getType(),
                category.getIcon(),
                category.getColor(),
                category.getCreatedAt()
        );
    }
}