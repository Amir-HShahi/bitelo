package dev.burgerman.bitelo.model.dto;

public record ResetPasswordRequest(String userId, String resetPasswordToken, String newPassword) {
}