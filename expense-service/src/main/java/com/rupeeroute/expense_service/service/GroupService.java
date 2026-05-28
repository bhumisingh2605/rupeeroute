package com.rupeeroute.expense_service.service;

import com.rupeeroute.expense_service.dto.GroupRequest;
import com.rupeeroute.expense_service.dto.GroupResponse;
import com.rupeeroute.expense_service.entity.Group;
import com.rupeeroute.expense_service.entity.GroupMember;
import com.rupeeroute.expense_service.entity.GroupMemberId;
import com.rupeeroute.expense_service.entity.User;
import com.rupeeroute.expense_service.repository.GroupMemberRepository;
import com.rupeeroute.expense_service.repository.GroupRepository;
import com.rupeeroute.expense_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    public GroupResponse createGroup(GroupRequest request) {
        User creator = userRepository.findById(request.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Group group = new Group();
        group.setName(request.getName());
        group.setCreatedBy(creator);
        Group saved = groupRepository.save(group);

        // Creator ko automatically group member banao
        GroupMember member = new GroupMember();
        GroupMemberId memberId = new GroupMemberId();
        memberId.setGroupId(saved.getId());
        memberId.setUserId(creator.getId());
        member.setId(memberId);
        member.setGroup(saved);
        member.setUser(creator);
        member.setJoinedAt(OffsetDateTime.now());
        groupMemberRepository.save(member);

        return mapToResponse(saved);
    }

    public GroupResponse getGroup(UUID id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        return mapToResponse(group);
    }

    public void addMember(UUID groupId, UUID userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GroupMemberId memberId = new GroupMemberId();
        memberId.setGroupId(groupId);
        memberId.setUserId(userId);

        GroupMember member = new GroupMember();
        member.setId(memberId);
        member.setGroup(group);
        member.setUser(user);
        member.setJoinedAt(OffsetDateTime.now());
        groupMemberRepository.save(member);
    }

    private GroupResponse mapToResponse(Group group) {
        GroupResponse response = new GroupResponse();
        response.setId(group.getId());
        response.setName(group.getName());
        response.setCreatedBy(group.getCreatedBy().getId());
        response.setCreatedByName(group.getCreatedBy().getName());
        response.setCreatedAt(group.getCreatedAt());
        return response;
    }
}