package dev.burgerman.bitelo.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request DTO for verifying the password reset code sent to the user.")
public record ForgetPasswordVerifyRequest(

        @Schema(description = "Unique identifier of the user verifying the password reset code.") String userId,

        @Schema(description = "Verification code sent to the user's phone.", example = "482913") String code) {
}
