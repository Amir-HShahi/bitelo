package dev.burgerman.bitelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import dev.burgerman.bitelo.model.User;
import dev.burgerman.bitelo.model.dto.AuthToken;
import dev.burgerman.bitelo.model.dto.LoginRequest;
import dev.burgerman.bitelo.model.dto.RefreshTokenRequest;
import dev.burgerman.bitelo.model.dto.RegistrationRequest;
import dev.burgerman.bitelo.model.dto.UserRegisterResponse;
import dev.burgerman.bitelo.services.AuthService;
import dev.burgerman.bitelo.services.UserService;
import dev.burgerman.bitelo.services.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthService authService;
    private final VerificationService verificationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegisterResponse registerUser(@RequestBody RegistrationRequest request) {
        User user = userService.registerUser(request);
        verificationService.initiatePhoneVerification(user);
        UserRegisterResponse dto = new UserRegisterResponse(user);
        return dto;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthToken loginUser(@RequestBody LoginRequest request) {
        User user = userService.loginUser(request);
        AuthToken token = authService.generateToken(user);
        return token;
    }

    @PostMapping("/logout")
    public String logoutUser(@RequestBody String entity) {

        return entity;
    }

    @PostMapping("/refresh-token")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthToken refreshUserToken(@RequestBody RefreshTokenRequest request) {
        return authService.refreshAccessToken(request.refreshToken());
    }

    @PostMapping("/validate-token")
    public String postMethodName(@RequestBody String entity) {
        // TODO: process POST request

        return entity;
    }

}
