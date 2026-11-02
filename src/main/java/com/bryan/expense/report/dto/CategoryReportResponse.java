package com.bryan.expense.report.dto;

public record CategoryReportResponse(
        String categoryId,
        String categoryName,
        String categoryColor,
        double total,
        long transactionCount
) {}