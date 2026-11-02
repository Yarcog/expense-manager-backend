package com.bryan.expense.admin.dto;

public record AdminStatsResponse(
        long totalUsers,
        long totalAdmins,
        long totalCategories,
        long totalTransactions
) {}