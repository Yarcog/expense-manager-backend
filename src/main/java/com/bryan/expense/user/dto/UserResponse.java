package com.bryan.expense.user.dto;

import com.bryan.expense.user.Role;
import com.bryan.expense.user.User;

public record UserResponse(
        String id,
        String email,
        String name,
        String currency,
        Role role
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId() != null ? user.getId().toHexString() : null,
                user.getEmail(),
                user.getName(),
                user.getCurrency(),
                user.getRole()
        );
    }
}