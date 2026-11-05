package com.bryan.expense.account.dto;

import com.bryan.expense.account.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AccountRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @NotNull(message = "El tipo es obligatorio")
        AccountType type,

        @NotNull(message = "El saldo inicial es obligatorio")
        Double initialBalance,

        String color
) {}
