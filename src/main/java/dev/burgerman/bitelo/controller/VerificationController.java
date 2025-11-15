package dev.burgerman.bitelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.burgerman.bitelo.model.User;
import dev.burgerman.bitelo.model.annotation.swagger.ApiInternalServerErrorResponse;
import dev.burgerman.bitelo.model.annotation.swagger.ApiNotFoundResponse;
import dev.burgerman.bitelo.model.annotation.swagger.ApiUnauthorizedResponse;
import dev.burgerman.bitelo.model.dto.AuthToken;
import dev.burgerman.bitelo.model.dto.SendCodeRequest;
import dev.burgerman.bitelo.model.dto.VerifyCodeRequest;
import dev.burgerman.bitelo.services.AuthService;
import dev.burgerman.bitelo.services.VerificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Phone Verification", description = "Endpoints for sending and verifying verification codes")
@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
@ApiInternalServerErrorResponse
public class VerificationController {
    private final VerificationService verificationService;
    private final AuthService authService;

    @PostMapping("/verify-code")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Verify phone number", description = "Verifies the code sent to the user's phone. On success, returns refresh and access tokens.")
    @ApiResponse(responseCode = "200", description = "Verification successful")
    @ApiUnauthorizedResponse
    @ApiNotFoundResponse
    public AuthToken verifyPhone(@Valid @RequestBody VerifyCodeRequest request) {
        User user = verificationService.verifyCode(request);
        return authService.generateToken(user);
    }

    @PostMapping("/send-code")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Send verification code", description = "Sends a verification code to the user’s phone.")
    @ApiResponse(responseCode = "202", description = "Verification code sent")
    @ApiNotFoundResponse
    public void sendCode(@Valid @RequestBody SendCodeRequest request) {
        verificationService.initiatePhoneVerification(request);
    }
}
