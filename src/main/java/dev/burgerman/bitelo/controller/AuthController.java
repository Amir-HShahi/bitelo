package dev.burgerman.bitelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import dev.burgerman.bitelo.model.User;
import dev.burgerman.bitelo.model.annotation.swagger.ApiConflictResponse;
import dev.burgerman.bitelo.model.annotation.swagger.ApiInternalServerErrorResponse;
import dev.burgerman.bitelo.model.annotation.swagger.ApiForbiddenResponse;
import dev.burgerman.bitelo.model.annotation.swagger.ApiNotFoundResponse;
import dev.burgerman.bitelo.model.annotation.swagger.ApiUnauthorizedResponse;
import dev.burgerman.bitelo.model.dto.AuthToken;
import dev.burgerman.bitelo.model.dto.LoginRequest;
import dev.burgerman.bitelo.model.dto.RefreshTokenRequest;
import dev.burgerman.bitelo.model.dto.RegistrationRequest;
import dev.burgerman.bitelo.model.dto.UserRegisterResponse;
import dev.burgerman.bitelo.services.AuthService;
import dev.burgerman.bitelo.services.UserService;
import dev.burgerman.bitelo.services.VerificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "User Authentication", description = "Endpoints to register, login, and manage authentication tokens")
@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
@ApiInternalServerErrorResponse
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final VerificationService verificationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user", description = "Registers a new user with a unique phone number and initiates phone verification.")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiConflictResponse
    public UserRegisterResponse registerUser(@Valid @RequestBody RegistrationRequest request) {
        User user = userService.registerUser(request);
        verificationService.initiatePhoneVerification(user);
        return new UserRegisterResponse(user);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "User login", description = "Authenticates a user using phone number and password, returns access and refresh tokens.")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiUnauthorizedResponse
    @ApiForbiddenResponse
    public AuthToken loginUser(@Valid @RequestBody LoginRequest request) {
        User user = userService.loginUser(request);
        return authService.generateToken(user);
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logs out the user (currently a placeholder endpoint).")
    @ApiResponse(responseCode = "200", description = "Logout successful")
    public String logoutUser(@RequestBody String entity) {
        return entity;
    }

    @PostMapping("/refresh-token")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Refresh access token", description = "Generates a new access token using a valid refresh token.")
    @ApiResponse(responseCode = "201", description = "Access token refreshed successfully")
    @ApiUnauthorizedResponse
    @ApiNotFoundResponse
    public AuthToken refreshUserToken(@RequestBody RefreshTokenRequest request) {
        return authService.refreshAccessToken(request.refreshToken());
    }

    @PostMapping("/validate-token")
    @Operation(summary = "Validate token", description = "Validates a token (currently a placeholder endpoint).")
    @ApiResponse(responseCode = "200", description = "Token validation processed")
    @ApiUnauthorizedResponse
    public String validateToken(@RequestBody String entity) {
        return entity;
    }
}