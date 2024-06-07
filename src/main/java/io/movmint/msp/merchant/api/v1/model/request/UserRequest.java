package io.movmint.msp.merchant.api.v1.model.request;

import io.movmint.msp.merchant.data.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "phone is required")
        String phone,
        @NotBlank(message = "email is required")
        @Email(message = "invalid email format.")
        String email,
        @NotBlank(message = "password is required")
        @Size(min = 8, max = 20, message = "password must be at least 8 characters long")
        String password,
        @NotNull(message = "role is required")
        UserRole role) {
}
