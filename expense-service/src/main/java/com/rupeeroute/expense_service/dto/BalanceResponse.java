package com.rupeeroute.expense_service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class BalanceResponse {
    private UUID userId;
    private String userName;
    private BigDecimal balance;  // + matlab tumhe milega, - matlab tumhe dena hai
}