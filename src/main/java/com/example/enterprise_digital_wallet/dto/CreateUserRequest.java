package com.example.enterprise_digital_wallet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(

        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 80, message = "Full name must be between 2 and 80 characters")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Phone number is required")
        @Size(min = 8, max = 20, message = "Phone number must be between 8 and 20 characters")
        String phoneNumber
) {
}