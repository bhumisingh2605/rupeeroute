package com.rupeeroute.expense_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ExpenseRequest {

    @NotNull(message = "Group ID is required")
    private UUID groupId;

    @NotNull(message = "Payer ID is required")
    private UUID paidBy;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Description is required")
    private String description;

    private String splitType;

    @Valid
    private List<SplitDetail> splits;

    @Data
    public static class SplitDetail {

        @NotNull(message = "User ID is required")
        private UUID userId;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        private BigDecimal amount;
    }
}