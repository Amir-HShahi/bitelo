package dev.burgerman.bitelo.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO containing TOTP setup information for a user.")
public record SetupKeysResponse(

        @Schema(description = "Secret key used to configure the user's TOTP authenticator app.", example = "JBSWY3DPEHPK3PXP")
        String setupKey,

        @Schema(
            description = "URL to a QR code image that can be scanned by the authenticator app to set up TOTP.",
             example = "otpauth://totp/Bitelo:user@example.com?secret=JBSWY3DPEHPK3PXP&issuer=Bitelo")
        String setupQRLink
) {}
