package com.bryan.expense.transaction.dto;

import com.bryan.expense.transaction.Transaction;
import com.bryan.expense.transaction.TransactionType;

import java.time.LocalDateTime;

public record TransactionResponse(
        String id,
        String categoryId,
        TransactionType type,
        Double amount,
        String description,
        LocalDateTime date,
        LocalDateTime createdAt
) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId() != null ? transaction.getId().toHexString() : null,
                transaction.getCategoryId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getDate(),
                transaction.getCreatedAt()
        );
    }
}