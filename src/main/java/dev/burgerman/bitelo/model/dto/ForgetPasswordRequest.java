package dev.burgerman.bitelo.model.dto;

import dev.burgerman.bitelo.model.annotation.PhoneNumber;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request DTO for initiating a password reset using the user's phone number.")
public record ForgetPasswordRequest(

        @Schema(description = "Registered phone number of the user requesting a password reset.", example = "+989123456789")
        @PhoneNumber
        String phoneNumber
) {}
