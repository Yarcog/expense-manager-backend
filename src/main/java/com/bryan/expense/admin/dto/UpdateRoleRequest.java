package com.bryan.expense.admin.dto;

import com.bryan.expense.user.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(
        @NotNull(message = "El rol es obligatorio")
        Role role
) {}