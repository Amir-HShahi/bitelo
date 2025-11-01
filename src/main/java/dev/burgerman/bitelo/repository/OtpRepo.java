package dev.burgerman.bitelo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.burgerman.bitelo.model.Otp;

@Repository
public interface OtpRepo extends JpaRepository<Otp, UUID> {
    // Finds an unverified OTP of a user, with a matching code
    Optional<Otp> findByCodeAndUserIdAndVerifiedFalse(
            String code, UUID userId);

    List<Otp> deleteByUserIdAndVerifiedIsFalse(UUID userId);

    void deleteByExpiresAtBefore(LocalDateTime now);
}
