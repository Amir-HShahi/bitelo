package dev.burgerman.bitelo.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request DTO for resending a verification code to a user.")
public record SendCodeRequest(

        @Schema(description = "Unique identifier of the user to whom the verification code will be sent.")
        String userId
) {}
