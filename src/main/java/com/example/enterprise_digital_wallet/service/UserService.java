package com.example.enterprise_digital_wallet.service;

import com.example.enterprise_digital_wallet.dto.CreateUserRequest;
import com.example.enterprise_digital_wallet.dto.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(UUID userId);

    List<UserResponse> getAllUsers();
}