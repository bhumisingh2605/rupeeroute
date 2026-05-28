package com.rupeeroute.settlement_service.repository;

import com.rupeeroute.settlement_service.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, UUID> {

    // Group ke saare settlements dekho
    List<Settlement> findByGroupId(UUID groupId);

    // Group ke pending settlements dekho
    List<Settlement> findByGroupIdAndStatus(UUID groupId, String status);

    // Group ke saare pending settlements delete karo (recalculate ke liye)
    void deleteByGroupIdAndStatus(UUID groupId, String status);
}