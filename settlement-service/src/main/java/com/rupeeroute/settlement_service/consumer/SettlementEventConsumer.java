package com.rupeeroute.settlement_service.consumer;

import com.rupeeroute.settlement_service.dto.ExpenseEvent;
import com.rupeeroute.settlement_service.service.SettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
@RequiredArgsConstructor
public class SettlementEventConsumer {

    private final SettlementService settlementService;
    private final StringRedisTemplate redisTemplate;

    @KafkaListener(
            topics = "rupeeroute.expense.added.v1",
            groupId = "settlement-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onExpenseAdded(
            @Payload ExpenseEvent event,
            Acknowledgment ack) {

        log.info("Received expense event: {}", event.getEventId());

        try {
            // Idempotency check — same event dobara process mat karo
            String key = "processed:settlement:" + event.getEventId();
            Boolean alreadyProcessed = redisTemplate.hasKey(key);

            if (Boolean.TRUE.equals(alreadyProcessed)) {
                log.warn("Duplicate event {}, skipping", event.getEventId());
                ack.acknowledge();
                return;
            }

            // Settlement recalculate karo
            settlementService.recalculate(
                    event.getGroupId(),
                    event.getShares(),
                    event.getPayerId(),
                    event.getTotalAmount()
            );

            // Mark as processed in Redis — 24h TTL
            redisTemplate.opsForValue().set(key, "1", Duration.ofHours(24));

            ack.acknowledge();
            log.info("Settlement recalculated for group {}", event.getGroupId());

        } catch (Exception e) {
            log.error("Failed to process event {}", event.getEventId(), e);
            // Do NOT ack — Kafka will redeliver
        }
    }
}