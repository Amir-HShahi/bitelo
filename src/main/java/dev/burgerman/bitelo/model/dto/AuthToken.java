package dev.burgerman.bitelo.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO containing a pair of tokens: access and refresh.")
public record AuthToken(

        @Schema(description = "Refresh token used to obtain a new access token when the current one expires.") String refreshToken,

        @Schema(description = "JWT access token used for authenticated API requests.") String accessToken) {
}
