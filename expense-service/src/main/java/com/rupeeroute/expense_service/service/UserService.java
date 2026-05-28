package com.rupeeroute.expense_service.service;

import com.rupeeroute.expense_service.dto.UserRequest;
import com.rupeeroute.expense_service.dto.UserResponse;
import com.rupeeroute.expense_service.entity.User;
import com.rupeeroute.expense_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUpiId(request.getUpiId());
        user.setPhone(request.getPhone());
        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    public UserResponse getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setUpiId(user.getUpiId());
        response.setPhone(user.getPhone());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}