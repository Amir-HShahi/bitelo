package dev.burgerman.bitelo.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dev.burgerman.bitelo.model.RefreshToken;
import dev.burgerman.bitelo.model.User;
import dev.burgerman.bitelo.model.exception.InvalidTokenException;
import dev.burgerman.bitelo.repository.RefreshTokenRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepo refreshTokenRepo;

    @Value("${jwt.refresh.expiration}")
    private Long refreshExpiration;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        log.info("Creating refreshToken for user: {}", user.getId());
        RefreshToken token = new RefreshToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUserId(user.getId());
        token.setExpiresAt(LocalDateTime.now().plusDays(30));
        token.setCreatedAt(LocalDateTime.now());
        token.setRevoked(false);
        return refreshTokenRepo.save(token);
    }

    @Transactional()
    public RefreshToken validateAndGet(String token) {
        RefreshToken refreshToken = refreshTokenRepo.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

        if (refreshToken.isRevoked()) {
            throw new InvalidTokenException("Refresh token has been revoked");
        }

        if (refreshToken.isExpired()) {
            throw new InvalidTokenException("Refresh token has expired");
        }

        return refreshToken;
    }

    @Transactional
    public void revoke(String token) {
        RefreshToken refreshToken = refreshTokenRepo.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

        refreshToken.setRevoked(true);
        refreshTokenRepo.save(refreshToken);
    }

    @Transactional
    public void revokeAllUserTokens(UUID userId) {
        List<RefreshToken> tokens = refreshTokenRepo.findByUserIdAndRevokedFalse(userId);
        tokens.forEach(token -> token.setRevoked(true));
        refreshTokenRepo.saveAll(tokens);
    }
}
