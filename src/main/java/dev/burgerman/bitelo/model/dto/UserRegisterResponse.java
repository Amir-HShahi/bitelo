package dev.burgerman.bitelo.model.dto;

import java.util.UUID;
import dev.burgerman.bitelo.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO returned after successful user registration.")
public record UserRegisterResponse(

        @Schema(description = "Unique identifier of the newly registered user.")
        UUID id,

        @Schema(description = "Registered phone number of the user.", example = "+989123456789")
        String phoneNumber,

        @Schema(description = "Indicates whether the user's phone number has been verified.", example = "false")
        boolean verified,

        @Schema(description = "Informational message about the registration process.", example = "Verification code sent to your phone")
        String message
) {
    public UserRegisterResponse(User user) {
        this(
                user.getId(),
                user.getPhoneNumber(),
                user.isVerified(),
                "Verification code sent to your phone"
        );
    }
}
