package com.bryan.expense.account.dto;

import com.bryan.expense.account.Account;
import com.bryan.expense.account.AccountType;

import java.time.LocalDateTime;

public record AccountResponse(
        String id,
        String name,
        AccountType type,
        Double initialBalance,
        Double balance,
        String color,
        LocalDateTime createdAt
) {
    public static AccountResponse from(Account account, Double balance) {
        return new AccountResponse(
                account.getId() != null ? account.getId().toHexString() : null,
                account.getName(),
                account.getType(),
                account.getInitialBalance(),
                balance,
                account.getColor(),
                account.getCreatedAt()
        );
    }
}
