package dev.burgerman.bitelo.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO returned after successful verification of the password reset code.")
public record ForgetPasswordVerifyResponse(

        @Schema(description = "Unique identifier of the user who verified the password reset code.") String userId,

        @Schema(description = "Temporary reset token used to authorize password update requests.") String resetToken) {
}
