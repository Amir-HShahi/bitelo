package dev.burgerman.bitelo.model.dto;

import dev.burgerman.bitelo.model.annotation.Password;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request DTO for resetting a user's password using a valid reset token.")
public record ResetPasswordRequest(

        @Schema(description = "Unique identifier of the user resetting the password.")
        String userId,

        @Schema(description = "ResetToken issued after verifying the password reset request.")
        @NotNull
        String resetPasswordToken,

        @Schema(description = "New password for the user's account.", example = "N3wP@ssw0rd!")
        @Password
        String newPassword
) {}
