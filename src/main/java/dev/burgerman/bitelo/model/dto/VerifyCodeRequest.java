package dev.burgerman.bitelo.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request DTO for verifying a code sent to the user during registration or password reset.")
public record VerifyCodeRequest(

        @Schema(description = "Unique identifier of the user verifying the code.")
        String userId,

        @Schema(description = "Verification code received by the user.", example = "482913")
        String code
) {}
