package com.rupeeroute.settlement_service.service;

import com.rupeeroute.settlement_service.dto.ExpenseEvent;
import com.rupeeroute.settlement_service.entity.Settlement;
import com.rupeeroute.settlement_service.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;

    @Transactional
    public List<Settlement> recalculate(UUID groupId,
                                        List<ExpenseEvent.ParticipantShare> shares,
                                        UUID payerId,
                                        BigDecimal totalAmount) {

        log.info("Recalculating settlements for group {}", groupId);

        // Step 1 — Delete existing pending settlements for this group
        settlementRepository.deleteByGroupIdAndStatus(
                groupId, "PENDING");

        // Step 2 — Build net balance map
        Map<UUID, BigDecimal> netBalance = new HashMap<>();
        netBalance.merge(payerId, totalAmount, BigDecimal::add);
        for (ExpenseEvent.ParticipantShare share : shares) {
            netBalance.merge(
                    share.getUserId(),
                    share.getAmount().negate(),
                    BigDecimal::add);
        }

        // Step 3 — Greedy algorithm — minimum transactions
        PriorityQueue<long[]> creditors = new PriorityQueue<>(
                (a, b) -> Long.compare(b[1], a[1])); // max heap
        PriorityQueue<long[]> debtors = new PriorityQueue<>(
                (a, b) -> Long.compare(a[1], b[1]));  // min heap

        List<UUID> users = new ArrayList<>(netBalance.keySet());
        for (int i = 0; i < users.size(); i++) {
            long paise = netBalance.get(users.get(i))
                    .multiply(BigDecimal.valueOf(100))
                    .longValue();
            if (paise > 0) creditors.offer(new long[]{i, paise});
            else if (paise < 0) debtors.offer(new long[]{i, paise});
        }

        // Step 4 — Match creditors and debtors
        List<Settlement> settlements = new ArrayList<>();
        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            long[] creditor = creditors.poll();
            long[] debtor   = debtors.poll();
            long transfer   = Math.min(creditor[1], -debtor[1]);

            Settlement settlement = Settlement.builder()
                    .groupId(groupId)
                    .fromUserId(users.get((int) debtor[0]))
                    .toUserId(users.get((int) creditor[0]))
                    .amount(BigDecimal.valueOf(transfer, 2))
                    .status("PENDING")
                    .createdAt(OffsetDateTime.now())
                    .build();

            settlements.add(settlement);

            creditor[1] -= transfer;
            debtor[1]   += transfer;

            if (creditor[1] > 0) creditors.offer(creditor);
            if (debtor[1]   < 0) debtors.offer(debtor);
        }

        // Step 5 — Save and return
        List<Settlement> saved = settlementRepository.saveAll(settlements);
        log.info("Saved {} settlements for group {}",
                saved.size(), groupId);
        return saved;
    }

    public List<Settlement> getGroupSettlements(UUID groupId) {
        return settlementRepository.findByGroupId(groupId);
    }

    public List<Settlement> getPendingSettlements(UUID groupId) {
        return settlementRepository.findByGroupIdAndStatus(
                groupId, "PENDING");
    }

    // Mark settlement as COMPLETED
    @Transactional
    public Settlement settlePayment(UUID settlementId) {
        Settlement settlement = settlementRepository
                .findById(settlementId)
                .orElseThrow(() ->
                        new RuntimeException("Settlement not found"));

        if ("COMPLETED".equals(settlement.getStatus())) {
            throw new RuntimeException("Settlement already completed");
        }

        settlement.setStatus("COMPLETED");
        settlement.setSettledAt(OffsetDateTime.now());
        return settlementRepository.save(settlement);
    }
}