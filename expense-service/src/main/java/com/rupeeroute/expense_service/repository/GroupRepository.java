package com.rupeeroute.expense_service.repository;

import com.rupeeroute.expense_service.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
}