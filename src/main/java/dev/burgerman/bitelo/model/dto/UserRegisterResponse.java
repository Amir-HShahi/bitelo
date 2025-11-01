package dev.burgerman.bitelo.model.dto;

import java.util.UUID;

import dev.burgerman.bitelo.model.User;

public record UserRegisterResponse(
        UUID id,
        String phoneNumber,
        boolean verified,
        String message) {
    public UserRegisterResponse(User user) {
        this(
                user.getId(),
                user.getPhoneNumber(),
                user.isVerified(),
                "Verification code sent to your phone");
    }
}
