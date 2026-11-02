package com.bryan.expense.report.dto;

public record SummaryResponse(
        int month,
        int year,
        double totalIncome,
        double totalExpense,
        double balance,
        long transactionCount
) {}