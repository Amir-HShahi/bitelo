package dev.burgerman.bitelo.services;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.burgerman.bitelo.model.Otp;
import dev.burgerman.bitelo.model.User;
import dev.burgerman.bitelo.model.dto.SendCodeRequest;
import dev.burgerman.bitelo.model.dto.VerifyCodeRequest;
import dev.burgerman.bitelo.model.exception.UserNotFoundException;
import dev.burgerman.bitelo.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class VerificationService {
    private final SmsService smsService;
    private final OtpService otpService;
    private final UserRepo userRepo;

    public void initiatePhoneVerification(SendCodeRequest request) {
        User user = userRepo.findById(UUID.fromString(request.userId()))
                .orElseThrow(() -> new UserNotFoundException("Could not find a user with phone number"));

        Otp otp = otpService.createOtp(user);
        smsService.sendOtpCode(user, otp);
    }

    public void initiatePhoneVerification(User user) {
        Otp otp = otpService.createOtp(user);
        smsService.sendOtpCode(user, otp);
    }

    @Transactional
    public User verifyCode(VerifyCodeRequest request) {
        User user = userRepo.findById(UUID.fromString(request.userId()))
                .orElseThrow(() -> new UserNotFoundException("Could not find a user with phone number"));

        otpService.validateUserOtp(user, request.code());

        user.setVerified(true);
        userRepo.save(user);
        return user;
    }
}
