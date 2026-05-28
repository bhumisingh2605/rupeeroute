package com.rupeeroute.settlement_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "settlements")
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Column(name = "from_user", nullable = false)
    private UUID fromUserId;

    @Column(name = "to_user", nullable = false)
    private UUID toUserId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String status;  // PENDING, COMPLETED, CANCELLED

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "settled_at")
    private OffsetDateTime settledAt;
}