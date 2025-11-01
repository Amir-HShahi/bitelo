package dev.burgerman.bitelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import dev.burgerman.bitelo.model.PasswordResetToken;
import dev.burgerman.bitelo.model.User;
import dev.burgerman.bitelo.model.dto.AuthToken;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
@Slf4j
public class PasswordController {
    private final PasswordService passwordService;
    private final VerificationService verificationService;
    private final AuthService authService;
    private final PasswordResetTokenService resetTokenService;

    @PostMapping("/forget")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ForgetPasswordResponse forgetPassword(@RequestBody ForgetPasswordRequest request) {
        User user = passwordService.initiatePasswordReset(request);
        verificationService.initiatePhoneVerification(user);
        return new ForgetPasswordResponse(user.getId().toString());
    }

    @PostMapping("/reset")
    public AuthToken resetPassword(@RequestBody ResetPasswordRequest request) {
        User user = passwordService.resetPassword(request);
        return authService.generateToken(user);
    }

    @PostMapping("/forget/verify-code")
    public ForgetPasswordVerifyResponse verifyCode(@RequestBody ForgetPasswordVerifyRequest request) {
        User user = verificationService.verifyCode(new VerifyCodeRequest(request.userId(), request.code()));
        PasswordResetToken token = resetTokenService.createToken(user);

        return new ForgetPasswordVerifyResponse(user.getId().toString(), token.getToken());
    }

    @PostMapping("/change")
    public String changePassword(@RequestBody String entity) {
        return entity;
    }
}
