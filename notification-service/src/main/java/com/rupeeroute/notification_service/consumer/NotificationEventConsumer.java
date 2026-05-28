package com.rupeeroute.notification_service.consumer;

import com.rupeeroute.notification_service.dto.ExpenseEvent;
import com.rupeeroute.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "rupeeroute.expense.added.v1",
            groupId = "notification-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onExpenseAdded(
            @Payload ExpenseEvent event,
            Acknowledgment ack) {

        log.info("Notification service received event: {}",
                event.getEventId());

        try {
            notificationService.notifyExpenseAdded(event);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed to send notification for event {}",
                    event.getEventId(), e);
            // Do NOT ack — Kafka will redeliver
        }
    }
}