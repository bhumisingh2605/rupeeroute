package com.rupeeroute.expense_service.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.UUID;

@Data
@Embeddable
public class GroupMemberId {
    private UUID groupId;
    private UUID userId;
}
