package com.rupeeroute.expense_service.controller;

import com.rupeeroute.expense_service.dto.BalanceResponse;
import com.rupeeroute.expense_service.dto.ExpenseRequest;
import com.rupeeroute.expense_service.dto.ExpenseResponse;
import com.rupeeroute.expense_service.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    // Expense add karo
    @PostMapping
    public ResponseEntity<ExpenseResponse> addExpense( @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(expenseService.addExpense(request));
    }

    // Group ke sab expenses dekho
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<ExpenseResponse>> getGroupExpenses(@PathVariable UUID groupId) {
        return ResponseEntity.ok(expenseService.getGroupExpenses(groupId));
    }

    // Group balances dekho
    @GetMapping("/group/{groupId}/balances")
    public ResponseEntity<List<BalanceResponse>> getGroupBalances(@PathVariable UUID groupId) {
        return ResponseEntity.ok(expenseService.getGroupBalances(groupId));
    }
}