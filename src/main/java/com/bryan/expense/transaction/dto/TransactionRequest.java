package com.bryan.expense.transaction.dto;

import com.bryan.expense.transaction.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record TransactionRequest(
        @NotBlank(message = "El ID de la categoría es obligatorio")
        String categoryId,

        @NotNull(message = "El tipo es obligatorio")
        TransactionType type,

        @NotNull(message = "El monto es obligatorio")
        @Positive(message = "El monto debe ser mayor que cero")
        Double amount,

        String description,

        @NotNull(message = "La fecha es obligatoria")
        LocalDateTime date
) {}