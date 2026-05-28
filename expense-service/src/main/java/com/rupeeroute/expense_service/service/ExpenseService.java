package com.rupeeroute.expense_service.service;

import com.rupeeroute.expense_service.dto.ExpenseRequest;
import com.rupeeroute.expense_service.dto.ExpenseResponse;
import com.rupeeroute.expense_service.dto.BalanceResponse;
import com.rupeeroute.expense_service.entity.*;
import com.rupeeroute.expense_service.event.ExpenseEventPublisher;
import com.rupeeroute.expense_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ExpenseEventPublisher expenseEventPublisher;

    @Transactional
    public ExpenseResponse addExpense(ExpenseRequest request) {
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User paidBy = userRepository.findById(request.getPaidBy())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Expense save karo
        Expense expense = new Expense();
        expense.setGroup(group);
        expense.setPaidBy(paidBy);
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setSplitType(request.getSplitType() != null ?
                request.getSplitType() : "EQUAL");
        Expense saved = expenseRepository.save(expense);

        // Splits calculate karo
        List<ExpenseSplit> splits = new ArrayList<>();

        if ("EQUAL".equals(saved.getSplitType())) {
            // Group ke sabhi members mein equally split karo
            List<GroupMember> members =
                    groupMemberRepository.findByGroupId(group.getId());
            BigDecimal splitAmount = request.getAmount()
                    .divide(BigDecimal.valueOf(members.size()),
                            2, RoundingMode.HALF_UP);

            for (GroupMember member : members) {
                ExpenseSplit split = new ExpenseSplit();
                split.setExpense(saved);
                split.setUser(member.getUser());
                split.setAmount(splitAmount);
                split.setSettled(false);
                splits.add(split);
            }
        } else {
            // CUSTOM split
            for (ExpenseRequest.SplitDetail detail : request.getSplits()) {
                User user = userRepository.findById(detail.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                ExpenseSplit split = new ExpenseSplit();
                split.setExpense(saved);
                split.setUser(user);
                split.setAmount(detail.getAmount());
                split.setSettled(false);
                splits.add(split);
            }
        }

        expenseSplitRepository.saveAll(splits);

        // Kafka event publish karo — settlement service consume karega
        expenseEventPublisher.publishExpenseAdded(saved, splits);

        return mapToResponse(saved, splits);
    }

    public List<ExpenseResponse> getGroupExpenses(UUID groupId) {
        List<Expense> expenses = expenseRepository.findByGroupId(groupId);
        return expenses.stream().map(expense -> {
            List<ExpenseSplit> splits =
                    expenseSplitRepository.findByExpenseId(expense.getId());
            return mapToResponse(expense, splits);
        }).collect(Collectors.toList());
    }

    public List<BalanceResponse> getGroupBalances(UUID groupId) {
        List<GroupMember> members =
                groupMemberRepository.findByGroupId(groupId);
        Map<UUID, BigDecimal> balanceMap = new HashMap<>();

        // Sabhi members ka balance 0 se shuru karo
        for (GroupMember member : members) {
            balanceMap.put(member.getUser().getId(), BigDecimal.ZERO);
        }

        // Sabhi expenses calculate karo
        List<Expense> expenses = expenseRepository.findByGroupId(groupId);
        for (Expense expense : expenses) {
            UUID paidById = expense.getPaidBy().getId();
            balanceMap.merge(paidById, expense.getAmount(), BigDecimal::add);

            List<ExpenseSplit> splits =
                    expenseSplitRepository.findByExpenseId(expense.getId());
            for (ExpenseSplit split : splits) {
                if (!split.getSettled()) {
                    UUID userId = split.getUser().getId();
                    balanceMap.merge(userId,
                            split.getAmount().negate(), BigDecimal::add);
                }
            }
        }

        return members.stream().map(member -> {
            BalanceResponse response = new BalanceResponse();
            response.setUserId(member.getUser().getId());
            response.setUserName(member.getUser().getName());
            response.setBalance(balanceMap.getOrDefault(
                    member.getUser().getId(), BigDecimal.ZERO));
            return response;
        }).collect(Collectors.toList());
    }

    private ExpenseResponse mapToResponse(Expense expense,
                                          List<ExpenseSplit> splits) {
        ExpenseResponse response = new ExpenseResponse();
        response.setId(expense.getId());
        response.setGroupId(expense.getGroup().getId());
        response.setPaidBy(expense.getPaidBy().getId());
        response.setPaidByName(expense.getPaidBy().getName());
        response.setAmount(expense.getAmount());
        response.setDescription(expense.getDescription());
        response.setSplitType(expense.getSplitType());
        response.setCreatedAt(expense.getCreatedAt());

        List<ExpenseResponse.SplitResponse> splitResponses = splits.stream()
                .map(split -> {
                    ExpenseResponse.SplitResponse sr =
                            new ExpenseResponse.SplitResponse();
                    sr.setUserId(split.getUser().getId());
                    sr.setUserName(split.getUser().getName());
                    sr.setAmount(split.getAmount());
                    sr.setSettled(split.getSettled());
                    return sr;
                }).collect(Collectors.toList());

        response.setSplits(splitResponses);
        return response;
    }
}