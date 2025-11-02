package dev.burgerman.bitelo.model.dto;

import dev.burgerman.bitelo.model.annotation.Password;
import jakarta.validation.constraints.NotNull;

public record ResetPasswordRequest(String userId, @NotNull String resetPasswordToken, @Password String newPassword) {
}