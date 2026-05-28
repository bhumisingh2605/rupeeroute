package com.rupeeroute.expense_service.controller;

import com.rupeeroute.expense_service.dto.GroupRequest;
import com.rupeeroute.expense_service.dto.GroupResponse;
import com.rupeeroute.expense_service.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    // Group banao
    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody GroupRequest request) {
        return ResponseEntity.ok(groupService.createGroup(request));
    }

    // Group dekho
    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable UUID id) {
        return ResponseEntity.ok(groupService.getGroup(id));
    }

    // Member add karo
    @PostMapping("/{groupId}/members/{userId}")
    public ResponseEntity<String> addMember(
            @PathVariable UUID groupId,
            @PathVariable UUID userId) {
        groupService.addMember(groupId, userId);
        return ResponseEntity.ok("Member added successfully");
    }
}