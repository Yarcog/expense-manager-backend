package com.bryan.expense.report;

import com.bryan.expense.category.Category;
import com.bryan.expense.category.CategoryRepository;
import com.bryan.expense.report.dto.CategoryReportResponse;
import com.bryan.expense.report.dto.SummaryResponse;
import com.bryan.expense.transaction.Transaction;
import com.bryan.expense.transaction.TransactionRepository;
import com.bryan.expense.transaction.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("Usuario no autenticado");
        }
        return (String) auth.getPrincipal();
    }

    public SummaryResponse getSummary(int month, int year) {
        String userId = getCurrentUserId();
        LocalDateTime from = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime to = from.plusMonths(1);

        // ✅ Obtenemos TODAS las transacciones del usuario en ese rango
        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(userId, from, to);

        double totalIncome = 0;
        double totalExpense = 0;

        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.INCOME) {
                totalIncome += t.getAmount();
            } else {
                totalExpense += t.getAmount();
            }
        }

        return new SummaryResponse(
                month,
                year,
                totalIncome,
                totalExpense,
                totalIncome - totalExpense,
                transactions.size()
        );
    }

    public List<CategoryReportResponse> getByCategory(int month, int year, TransactionType type) {
        String userId = getCurrentUserId();
        LocalDateTime from = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime to = from.plusMonths(1);

        TransactionType filterType = (type != null) ? type : TransactionType.EXPENSE;

        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(userId, from, to)
                .stream()
                .filter(t -> t.getType() == filterType)
                .toList();

        // Agrupar por categoría
        Map<String, Double> totals = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategoryId,
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        List<CategoryReportResponse> response = new ArrayList<>();

        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            String categoryId = entry.getKey();
            Double total = entry.getValue();

            long count = transactions.stream()
                    .filter(t -> t.getCategoryId().equals(categoryId))
                    .count();

            Category category = categoryRepository.findById(new org.bson.types.ObjectId(categoryId)).orElse(null);
            String categoryName = (category != null) ? category.getName() : "Desconocida";
            String categoryColor = (category != null) ? category.getColor() : "#CCCCCC";

            response.add(new CategoryReportResponse(
                    categoryId, categoryName, categoryColor, total, count
            ));
        }

        // Ordenar de mayor a menor
        response.sort((a, b) -> Double.compare(b.total(), a.total()));

        return response;
    }
}