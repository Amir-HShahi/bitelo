package dev.burgerman.bitelo.model.dto;

import dev.burgerman.bitelo.model.annotation.CountryCode;
import dev.burgerman.bitelo.model.annotation.Password;
import dev.burgerman.bitelo.model.annotation.PhoneNumber;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request DTO for authenticating a user using phone number and password.")
public record LoginRequest(

        @Schema(description = "Country calling code of the user's phone number.", example = "+98")
        @CountryCode
        String countryCode,

        @Schema(description = "User's registered phone number.", example = "9123456789")
        @PhoneNumber
        String phoneNumber,

        @Schema(description = "User's password.", example = "P@ssw0rd123")
        @Password
        String password
) {}