package com.rupeeroute.expense_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "upi_id", length = 50)
    private String upiId;

    @Column(length = 15, unique = true)
    private String phone;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}