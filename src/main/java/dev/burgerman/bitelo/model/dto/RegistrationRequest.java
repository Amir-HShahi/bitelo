package dev.burgerman.bitelo.model.dto;

import dev.burgerman.bitelo.model.annotation.CountryCode;
import dev.burgerman.bitelo.model.annotation.Password;
import dev.burgerman.bitelo.model.annotation.PhoneNumber;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request DTO for registering a new user with phone number and password.")
public record RegistrationRequest(

        @Schema(description = "Country calling code of the user's phone number.", example = "+98")
        @CountryCode
        String countryCode,

        @Schema(description = "User's phone number to register.", example = "9123456789")
        @PhoneNumber
        String phoneNumber,

        @Schema(description = "Password for the new user account.", example = "P@ssw0rd123")
        @Password
        String password
) {}
