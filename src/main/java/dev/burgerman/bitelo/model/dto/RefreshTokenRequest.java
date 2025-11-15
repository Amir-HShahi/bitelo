package dev.burgerman.bitelo.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request DTO for obtaining a new access token using a refresh token.")
public record RefreshTokenRequest(

        @Schema(description = "Valid refresh token previously issued to the user.")
        String refreshToken
) {}
