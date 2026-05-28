package com.rupeeroute.expense_service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class SettlementResponse {
    private UUID id;
    private UUID fromUserId;
    private String fromUserName;
    private UUID toUserId;
    private String toUserName;
    private BigDecimal amount;
    private String status;
    private OffsetDateTime createdAt;
}