package dev.burgerman.bitelo.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.burgerman.bitelo.model.Otp;
import dev.burgerman.bitelo.model.User;
import dev.burgerman.bitelo.model.exception.BadRequestException;
import dev.burgerman.bitelo.repository.OtpRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OtpService {
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRATION_MINUTES = 5;

    private final OtpRepo otpRepo;
    private final SecureRandom random = new SecureRandom();

    private String generateCode() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    @Transactional
    public Otp createOtp(User user) {
        invalidateExistingOtps(user.getId());

        log.info("Creating new OTP for user: {}", user.getId());
        String code = generateCode();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(OTP_EXPIRATION_MINUTES);

        Otp otp = new Otp();
        otp.setCode(code);
        otp.setCreatedAt(now);
        otp.setExpiresAt(expiresAt);
        otp.setVerified(false);
        otp.setUser(user);

        return otpRepo.save(otp);
    }

    @Transactional
    public void validateUserOtp(User user, String code) {
        String userId = user.getId().toString();
        log.info("Finding valid OTP for user: {}", userId);
        Optional<Otp> otpOptional = otpRepo.findByCodeAndUserIdAndVerifiedFalse(code, user.getId());

        if (otpOptional.isEmpty()) {
            log.warn("Provided code of user: {} is invalid");
            throw new BadRequestException("Invalid code");
        }
        Otp otp = otpOptional.get();

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Provided code of user: {} has expired", userId);
            throw new BadRequestException("Code has expired");
        }

        log.info("Provided code of user: {} is valid", userId);
        otp.setVerified(true);
        otpRepo.save(otp);
    }

    private void invalidateExistingOtps(UUID userId) {
        log.info("Invalidating existing OTPs of userId {}", userId);
        otpRepo.deleteByUserIdAndVerifiedIsFalse(userId);
    }

    @Scheduled(fixedRate = 3600000)
    private void cleanUpInvalidOtps() {
        log.info("Cleaning expired OTPs");
        otpRepo.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
