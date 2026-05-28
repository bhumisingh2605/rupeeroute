package com.rupeeroute.expense_service.dto;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String name;
    private String upiId;
    private String phone;
    private OffsetDateTime createdAt;
}