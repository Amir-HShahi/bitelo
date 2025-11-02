package dev.burgerman.bitelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.burgerman.bitelo.model.User;
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

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
public class VerificationController {
    private final VerificationService verificationService;
    private final AuthService authService;

    @PostMapping("/verify-code")
    @ResponseStatus(HttpStatus.OK)
    public AuthToken verifyPhone(@Valid @RequestBody VerifyCodeRequest request) {
        User user = verificationService.verifyCode(request);
        return authService.generateToken(user);
    }

    @PostMapping("/send-code")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendCode(@Valid @RequestBody SendCodeRequest request) {
        verificationService.initiatePhoneVerification(request);
    }
}
