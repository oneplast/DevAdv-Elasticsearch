package io.eddie.accountservice.model.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAccountRequest(
        @NotBlank
        String username,
        @NotBlank
        String password,
        @NotBlank
        String email
) {
}
