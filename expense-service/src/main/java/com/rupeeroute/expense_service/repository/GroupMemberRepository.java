package com.rupeeroute.expense_service.repository;

import com.rupeeroute.expense_service.entity.GroupMember;
import com.rupeeroute.expense_service.entity.GroupMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {
    List<GroupMember> findByGroupId(UUID groupId);
}