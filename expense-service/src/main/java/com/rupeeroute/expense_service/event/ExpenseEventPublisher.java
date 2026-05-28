package com.rupeeroute.expense_service.event;

import com.rupeeroute.expense_service.entity.Expense;
import com.rupeeroute.expense_service.entity.ExpenseSplit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseEventPublisher {

    private static final String TOPIC = "rupeeroute.expense.added.v1";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishExpenseAdded(Expense expense,
                                    List<ExpenseSplit> splits) {
        ExpenseEvent event = ExpenseEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .groupId(expense.getGroup().getId())
                .expenseId(expense.getId())
                .payerId(expense.getPaidBy().getId())
                .totalAmount(expense.getAmount())
                .shares(splits.stream()
                        .map(s -> new ExpenseEvent.ParticipantShare(
                                s.getUser().getId(),
                                s.getAmount()))
                        .toList())
                .eventType("EXPENSE_ADDED")
                .build();

        kafkaTemplate.send(TOPIC, expense.getGroup().getId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish expense event: {}",
                                ex.getMessage());
                    } else {
                        log.info("Published expense event {} to partition {}",
                                event.getEventId(),
                                result.getRecordMetadata().partition());
                    }
                });
    }
}