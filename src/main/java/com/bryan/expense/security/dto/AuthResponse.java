package com.bryan.expense.security.dto;

import com.bryan.expense.user.dto.UserResponse;

public record AuthResponse(
        String token,
        UserResponse user
) {}