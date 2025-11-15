package dev.burgerman.bitelo.services;

import org.springframework.stereotype.Service;

import dev.burgerman.bitelo.model.RefreshToken;
import dev.burgerman.bitelo.model.User;
import dev.burgerman.bitelo.model.dto.AuthToken;
import dev.burgerman.bitelo.model.exception.UserNotFoundException;
import dev.burgerman.bitelo.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenService jwtTokenService;
    private final UserRepo userRepo;

    public AuthToken generateToken(User user) {
        String accessToken = jwtTokenService.generateAccessToken(user.getPhoneNumber());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        AuthToken authToken = new AuthToken(refreshToken.getToken(), accessToken);
        return authToken;
    }

    public AuthToken refreshAccessToken(String refreshToken) {
        RefreshToken validatedToken = refreshTokenService.validateAndGet(refreshToken);

        User user = userRepo.findById(validatedToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String newAccessToken = jwtTokenService.generateAccessToken(user.getPhoneNumber());

        return new AuthToken(refreshToken, newAccessToken);
    }
}
