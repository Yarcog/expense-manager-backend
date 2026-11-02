package com.bryan.expense.category.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank(message = "El nombre es obligatorio") String name,
        @NotBlank(message = "El tipo es obligatorio") String type,
        String icon,
        String color
) {}