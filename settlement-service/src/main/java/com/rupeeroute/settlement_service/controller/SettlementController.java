package com.rupeeroute.settlement_service.controller;

import com.rupeeroute.settlement_service.entity.Settlement;
import com.rupeeroute.settlement_service.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    // Group ke saare settlements dekho
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Settlement>> getGroupSettlements(
            @PathVariable UUID groupId) {
        return ResponseEntity.ok(
                settlementService.getGroupSettlements(groupId));
    }

    // Group ke sirf pending settlements dekho
    @GetMapping("/group/{groupId}/pending")
    public ResponseEntity<List<Settlement>> getPendingSettlements(
            @PathVariable UUID groupId) {
        return ResponseEntity.ok(
                settlementService.getPendingSettlements(groupId));
    }

    // Settlement mark as COMPLETED
    @PatchMapping("/{settlementId}/settle")
    public ResponseEntity<Settlement> settlePayment(
            @PathVariable UUID settlementId) {
        return ResponseEntity.ok(
                settlementService.settlePayment(settlementId));
    }
}