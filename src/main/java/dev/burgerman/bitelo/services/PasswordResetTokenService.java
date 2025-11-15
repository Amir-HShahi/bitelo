package dev.burgerman.bitelo.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.burgerman.bitelo.model.PasswordResetToken;
import dev.burgerman.bitelo.model.User;
import dev.burgerman.bitelo.model.exception.BadRequestException;
import dev.burgerman.bitelo.repository.PasswordResetTokenRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PasswordResetTokenService {
    private final PasswordResetTokenRepo tokensRepo;

    @Value("${password-reset.token-expiry-hours}")
    private int tokenExpiryHours;

    private final SecureRandom random = new SecureRandom();

    public PasswordResetToken createToken(User user) {
        invalidateExistingTokens(user);

        String token = generateToken();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(tokenExpiryHours));

        return tokensRepo.save(resetToken);
    }

    public PasswordResetToken getPasswordResetToken(String token) {
        Optional<PasswordResetToken> optionalResetToken = tokensRepo.findByTokenAndUsedFalse(token);

        if (optionalResetToken.isEmpty())
            throw new BadRequestException("Invalid token");

        PasswordResetToken resetToken = optionalResetToken.get();

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new BadRequestException("Token is expired");

        return optionalResetToken.get();
    }

    public void markTokenAsUsed(PasswordResetToken resetToken) {
        resetToken.setUsed(true);
        tokensRepo.save(resetToken);
    }

    public User getUserByToken(String token) {
        getPasswordResetToken(token);
        return tokensRepo.findByTokenAndUsedFalse(token)
                .map(PasswordResetToken::getUser)
                .orElseThrow(() -> new BadCredentialsException("Couldn't find user with provided token"));
    }

    private void invalidateExistingTokens(User user) {
        tokensRepo.findByUserAndUsedFalse(user).ifPresent(token -> {
            token.setUsed(true);
            tokensRepo.save(token);
        });
    };

    private String generateToken() {
        byte[] tokenBytes = new byte[32];
        random.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    @Scheduled(fixedRate = 3600000)
    private void cleanUpExpiredTokens() {
        tokensRepo.deleteByExpiresAtBefore(LocalDateTime.now());
    }

}