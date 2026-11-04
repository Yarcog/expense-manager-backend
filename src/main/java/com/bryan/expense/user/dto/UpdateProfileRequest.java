package com.bryan.expense.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @NotBlank(message = "La moneda es obligatoria")
        @Size(min = 3, max = 3, message = "La moneda debe tener 3 caracteres")
        String currency
) {}
