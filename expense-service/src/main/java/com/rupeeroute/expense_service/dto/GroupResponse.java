package com.rupeeroute.expense_service.dto;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class GroupResponse {
    private UUID id;
    private String name;
    private UUID createdBy;
    private String createdByName;
    private OffsetDateTime createdAt;
}