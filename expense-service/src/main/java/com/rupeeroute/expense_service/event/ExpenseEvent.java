package com.rupeeroute.expense_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseEvent {
    private String eventId;
    private UUID groupId;
    private UUID expenseId;
    private UUID payerId;
    private BigDecimal totalAmount;
    private List<ParticipantShare> shares;
    private String eventType;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParticipantShare {
        private UUID userId;
        private BigDecimal amount;
    }
}