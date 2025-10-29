package io.eddie.accountservice.model.dto;

public record SignInRequest(
        String username,
        String password
) {
}
