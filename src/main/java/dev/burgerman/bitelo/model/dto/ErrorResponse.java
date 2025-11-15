package dev.burgerman.bitelo.model.dto;

import java.time.LocalDateTime;
import java.util.Map;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Schema(description = "Standard error response returned by the API when an exception occurs.")
@Getter
@Setter
@RequiredArgsConstructor
public class ErrorResponse {

    @Schema(description = "Timestamp when the error occurred.", example = "2025-11-13T18:42:31.123")
    private final LocalDateTime timeStamp = LocalDateTime.now();

    @Schema(description = "Short description or message explaining the error for user.", example = "User with this Phone Number already exists")
    private final String message;

    @Schema(description = "Request path where the error occurred.", example = "/api/register")
    private final String path;

    @Schema(description = "Unique trace identifier for correlating logs or debugging.", example = "9b23e10a3f6743a48d1b456ef7a9c2d5")
    private final String traceId;

    @Schema(description = "Map of field-specific validation errors, if any.", example = "{\"password\": \"Password must contain symbols\"}")
    private Map<String, String> fieldErrors;
}
