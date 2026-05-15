package com.example.enterprise_digital_wallet.controller;

import com.example.enterprise_digital_wallet.dto.ApiResponse;
import com.example.enterprise_digital_wallet.dto.CreateUserRequest;
import com.example.enterprise_digital_wallet.dto.UserResponse;
import com.example.enterprise_digital_wallet.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ApiResponse.success("User created successfully", response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUserById(@PathVariable UUID userId) {
        UserResponse response = userService.getUserById(userId);
        return ApiResponse.success("User fetched successfully", response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers() {
        List<UserResponse> response = userService.getAllUsers();
        return ApiResponse.success("Users fetched successfully", response);
    }
}