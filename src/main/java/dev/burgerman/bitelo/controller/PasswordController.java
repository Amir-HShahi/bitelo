package dev.burgerman.bitelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import dev.burgerman.bitelo.model.PasswordResetToken;
import dev.burgerman.bitelo.model.User;
import dev.burgerman.bitelo.model.annotation.swagger.ApiInternalServerErrorResponse;
import dev.burgerman.bitelo.model.annotation.swagger.ApiBearerAuth;
import dev.burgerman.bitelo.model.annotation.swagger.ApiForbiddenResponse;
import dev.burgerman.bitelo.model.annotation.swagger.ApiNotFoundResponse;
import dev.burgerman.bitelo.model.annotation.swagger.ApiUnauthorizedResponse;
import dev.burgerman.bitelo.model.dto.AuthToken;
import dev.burgerman.bitelo.model.dto.ChangePasswordRequest;
import dev.burgerman.bitelo.model.dto.ForgetPasswordRequest;
import dev.burgerman.bitelo.model.dto.ForgetPasswordResponse;
import dev.burgerman.bitelo.model.dto.ForgetPasswordVerifyRequest;
import dev.burgerman.bitelo.model.dto.ForgetPasswordVerifyResponse;
import dev.burgerman.bitelo.model.dto.ResetPasswordRequest;
import dev.burgerman.bitelo.model.dto.VerifyCodeRequest;
import dev.burgerman.bitelo.services.AuthService;
import dev.burgerman.bitelo.services.PasswordResetTokenService;
import dev.burgerman.bitelo.services.PasswordService;
import dev.burgerman.bitelo.services.VerificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Password management", description = "Endpoints to recover and reset password")
@RestController
@RequestMapping("/api/password")
@Slf4j
@RequiredArgsConstructor
@ApiInternalServerErrorResponse
public class PasswordController {
    private final PasswordService passwordService;
    private final VerificationService verificationService;
    private final AuthService authService;
    private final PasswordResetTokenService resetTokenService;

    @PostMapping("/forget")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Start password reset process", description = "Initiates password reset and sends verification code to user's phone.")
    @ApiResponse(responseCode = "202", description = "Verification code sent")
    @ApiNotFoundResponse
    public ForgetPasswordResponse forgetPassword(@Valid @RequestBody ForgetPasswordRequest request) {
        User user = passwordService.initiatePasswordReset(request);
        verificationService.initiatePhoneVerification(user);
        return new ForgetPasswordResponse(user.getId().toString());
    }

    @PostMapping("/reset")
    @Operation(summary = "Reset password using reset token", description = "Resets the password using a valid reset token and returns new auth token.")
    @ApiResponse(responseCode = "200", description = "Password reset successfully")
    @ApiUnauthorizedResponse
    @ApiForbiddenResponse
    public AuthToken resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        User user = passwordService.resetPassword(request);
        return authService.generateToken(user);
    }

    @PostMapping("/forget/verify-code")
    @Operation(summary = "Verify SMS code during password reset", description = "Verifies the code sent to user's phone and returns a password reset token.")
    @ApiResponse(responseCode = "200", description = "Code verified")
    @ApiUnauthorizedResponse
    @ApiNotFoundResponse
    public ForgetPasswordVerifyResponse verifyCode(@Valid @RequestBody ForgetPasswordVerifyRequest request) {
        User user = verificationService.verifyCode(
                new VerifyCodeRequest(request.userId(), request.code()));
        PasswordResetToken token = resetTokenService.createToken(user);
        return new ForgetPasswordVerifyResponse(user.getId().toString(), token.getToken());
    }

    @PatchMapping("/change")
    @Operation(summary = "Change password for authenticated user", description = "Changes password for currently authenticated user.")
    @ApiResponse(responseCode = "204", description = "Password updated. No content to return")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiUnauthorizedResponse
    @ApiForbiddenResponse
    @ApiBearerAuth
    public void changePassword(@AuthenticationPrincipal User user, @Valid @RequestBody ChangePasswordRequest request) {
        passwordService.updateUserPassword(request.newPassword(), user);
    }
}
