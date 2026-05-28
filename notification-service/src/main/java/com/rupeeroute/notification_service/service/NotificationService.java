package com.rupeeroute.notification_service.service;

import com.rupeeroute.notification_service.dto.ExpenseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void notifyExpenseAdded(ExpenseEvent event) {
        // Main notification log
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📢 NEW EXPENSE NOTIFICATION");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("Group ID   : {}", event.getGroupId());
        log.info("Paid By    : {}", event.getPayerId());
        log.info("Total      : Rs {}", event.getTotalAmount());
        log.info("Event Type : {}", event.getEventType());
        log.info("─────────────────────────────────────────");

        // Har participant ko notify karo
        if (event.getShares() != null) {
            log.info("Split Details:");
            for (ExpenseEvent.ParticipantShare share : event.getShares()) {
                log.info("  👤 User {} owes Rs {}",
                        share.getUserId(),
                        share.getAmount());
            }
        }

        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
