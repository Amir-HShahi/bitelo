package dev.burgerman.bitelo.model.dto;

import dev.burgerman.bitelo.model.annotation.Password;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request DTO to change password for authenticated user")
public record ChangePasswordRequest(
    @Password
    @Schema(description = "New password to be set as user's password", example = "Burger6666!")
    String newPassword
) {}
