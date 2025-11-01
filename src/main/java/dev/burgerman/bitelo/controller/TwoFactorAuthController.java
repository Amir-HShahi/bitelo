package dev.burgerman.bitelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.burgerman.bitelo.model.User;
import dev.burgerman.bitelo.model.dto.SetupKeysResponse;
import dev.burgerman.bitelo.model.dto.VerifyTOTPRequest;
import dev.burgerman.bitelo.services.Google2FAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/2fa")
@Slf4j
@RequiredArgsConstructor
public class TwoFactorAuthController {
    private final Google2FAService google2faService;

    @PostMapping("/setup-keys")
    public SetupKeysResponse setupCode(@AuthenticationPrincipal User user) {
        String secret = user.getGoogle2FASecretCode();
        String URL = google2faService.generateSetupKey(user);
        return new SetupKeysResponse(secret, URL);
    }

    @PostMapping("/enable")
    public String enable2FA(@RequestBody String entity) {
        return entity;
    }

    @PostMapping("/disable")
    public String disable2FA(@RequestBody String entity) {
        return entity;
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyTOTP(@AuthenticationPrincipal User user, @RequestBody VerifyTOTPRequest request) {
        boolean isValid = google2faService.verifyCode(user, request.code());
        return isValid ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @GetMapping("/backup-codes")
    public String generateBackupCodes() {
        return new String();
    }

    @PostMapping("/verify-backup")
    public String verifyBackupCode(@RequestBody String entity) {
        return entity;
    }

    @PostMapping("/regenerate-backup")
    public String regenerateBackupCodes(@RequestBody String entity) {
        return entity;
    }

    @GetMapping("/status")
    public String statusOf2FA(@RequestParam String param) {
        return new String();
    }
}
