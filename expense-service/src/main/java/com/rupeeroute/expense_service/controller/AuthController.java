package com.rupeeroute.expense_service.controller;

import com.rupeeroute.expense_service.dto.AuthRequest;
import com.rupeeroute.expense_service.dto.AuthResponse;
import com.rupeeroute.expense_service.dto.UserRequest;
import com.rupeeroute.expense_service.dto.UserResponse;
import com.rupeeroute.expense_service.security.JwtService;
import com.rupeeroute.expense_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    // Register — naya user banao
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
           @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    // Login — JWT token lo
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
          @Valid  @RequestBody AuthRequest request) {

        // Email + password validate karo
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Token generate karo
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(token));
    }
}