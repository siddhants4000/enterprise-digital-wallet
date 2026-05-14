package com.example.enterprise_digital_wallet.service;

import com.example.enterprise_digital_wallet.dto.CreateUserRequest;
import com.example.enterprise_digital_wallet.dto.UserResponse;
import com.example.enterprise_digital_wallet.entity.AppUser;
import com.example.enterprise_digital_wallet.entity.Wallet;
import com.example.enterprise_digital_wallet.exception.ResourceAlreadyExistsException;
import com.example.enterprise_digital_wallet.repository.AppUserRepository;
import com.example.enterprise_digital_wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.enterprise_digital_wallet.exception.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AppUserRepository appUserRepository;
    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        validateUserDoesNotExist(request);

        AppUser user = AppUser.builder()
                .fullName(request.fullName())
                .email(request.email().toLowerCase())
                .phoneNumber(request.phoneNumber())
                .build();

        AppUser savedUser = appUserRepository.save(user);

        Wallet wallet = Wallet.builder()
                .user(savedUser)
                .balance(BigDecimal.ZERO)
                .currency("EUR")
                .build();

        walletRepository.save(wallet);

        return mapToUserResponse(savedUser);
    }

    private void validateUserDoesNotExist(CreateUserRequest request) {
        if (appUserRepository.existsByEmail(request.email().toLowerCase())) {
            throw new ResourceAlreadyExistsException("User already exists with this email");
        }

        if (appUserRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new ResourceAlreadyExistsException("User already exists with this phone number");
        }
    }

    private UserResponse mapToUserResponse(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }

    @Override
    public UserResponse getUserById(UUID userId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapToUserResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return appUserRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .toList();
    }
}