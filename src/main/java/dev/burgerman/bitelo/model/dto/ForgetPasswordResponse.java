package dev.burgerman.bitelo.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO returned after initiating a successful password reset request.")
public record ForgetPasswordResponse(

        @Schema(description = "Unique identifier of the user associated with the password reset request.") String userId) {
}
