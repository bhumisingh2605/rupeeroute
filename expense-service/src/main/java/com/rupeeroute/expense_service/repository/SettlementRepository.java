package com.rupeeroute.expense_service.repository;

import com.rupeeroute.expense_service.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, UUID> {
    List<Settlement> findByGroupId(UUID groupId);
    List<Settlement> findByFromUserId(UUID userId);
    List<Settlement> findByToUserId(UUID userId);
}