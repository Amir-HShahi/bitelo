package dev.burgerman.bitelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.burgerman.bitelo.model.User;
import dev.burgerman.bitelo.model.annotation.swagger.ApiInternalServerErrorResponse;
import dev.burgerman.bitelo.model.annotation.swagger.ApiBearerAuth;
import dev.burgerman.bitelo.model.annotation.swagger.ApiForbiddenResponse;
import dev.burgerman.bitelo.model.annotation.swagger.ApiUnauthorizedResponse;
import dev.burgerman.bitelo.model.dto.SetupKeysResponse;
import dev.burgerman.bitelo.model.dto.VerifyTOTPRequest;
import dev.burgerman.bitelo.services.Google2FAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Tag(name = "Two-Factor Authentication", description = "Endpoints for Google Authenticator (TOTP)")
@RestController
@RequestMapping("/api/2fa")
@Slf4j
@RequiredArgsConstructor
@ApiInternalServerErrorResponse
@ApiBearerAuth
public class TwoFactorAuthController {
        private final Google2FAService google2faService;

        @GetMapping("/setup-keys")
        @Operation(summary = "Generate 2FA setup keys", description = "Returns the user's TOTP secret and Google Authenticator QR code link")
        @ApiResponse(responseCode = "200", description = "Setup keys generated successfully")
        @ApiForbiddenResponse
        public SetupKeysResponse setupCode(@AuthenticationPrincipal User user) {
                String secret = user.getGoogle2FASecretCode();
                String url = google2faService.generateSetupKey(user);
                return new SetupKeysResponse(secret, url);
        }

        @PostMapping("/verify")
        @Operation(summary = "Verify 2FA TOTP code", description = "Checks if the provided TOTP code is valid for the authenticated user")
        @ApiResponse(responseCode = "200", description = "TOTP code is valid")
        @ApiUnauthorizedResponse
        @ApiResponse(responseCode = "400", description = "Invalid TOTP code")
        public ResponseEntity<?> verifyTOTP(
                        @AuthenticationPrincipal User user,
                        @RequestBody VerifyTOTPRequest request) {

                boolean isValid = google2faService.verifyCode(user, request.code());
                return isValid ? ResponseEntity.ok().build()
                                : ResponseEntity.badRequest().build();
        }
}
