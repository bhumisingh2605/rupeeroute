package com.rupeeroute.expense_service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ExpenseResponse {
    private UUID id;
    private UUID groupId;
    private UUID paidBy;
    private String paidByName;
    private BigDecimal amount;
    private String description;
    private String splitType;
    private List<SplitResponse> splits;
    private OffsetDateTime createdAt;

    @Data
    public static class SplitResponse {
        private UUID userId;
        private String userName;
        private BigDecimal amount;
        private Boolean settled;
    }
}