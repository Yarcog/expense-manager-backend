package com.bryan.expense.report;

import com.bryan.expense.report.dto.CategoryReportResponse;
import com.bryan.expense.report.dto.SummaryResponse;
import com.bryan.expense.transaction.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService service;

    @GetMapping("/summary")
    public SummaryResponse getSummary(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        LocalDate now = LocalDate.now();
        int m = (month != null) ? month : now.getMonthValue();
        int y = (year != null) ? year : now.getYear();
        return service.getSummary(m, y);
    }

    @GetMapping("/by-category")
    public List<CategoryReportResponse> getByCategory(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) TransactionType type
    ) {
        LocalDate now = LocalDate.now();
        int m = (month != null) ? month : now.getMonthValue();
        int y = (year != null) ? year : now.getYear();
        return service.getByCategory(m, y, type);
    }
}