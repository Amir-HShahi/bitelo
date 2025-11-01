package dev.burgerman.bitelo.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.burgerman.bitelo.model.PasswordResetToken;
import dev.burgerman.bitelo.model.User;

@Repository
public interface PasswordResetTokenRepo extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);

    Optional<PasswordResetToken> findByUserAndUsedFalse(User user);

    void deleteByExpiresAtBefore(LocalDateTime dateTime);

    void deleteByUser(User user);
}
