package dev.burgerman.bitelo.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.burgerman.bitelo.model.PasswordResetToken;
import dev.burgerman.bitelo.model.User;
import dev.burgerman.bitelo.model.dto.ForgetPasswordRequest;
import dev.burgerman.bitelo.model.dto.ResetPasswordRequest;
import dev.burgerman.bitelo.model.exception.UserNotFoundException;
import dev.burgerman.bitelo.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordService {
    private final PasswordResetTokenService resetTokenService;
    private final UserRepo userRepo;

    @Value("${app.security.bcrypt.strength}")
    private int bcryptStrength;

    public User initiatePasswordReset(ForgetPasswordRequest request) {
        Optional<User> optionalUser = userRepo.findByPhoneNumber(request.phoneNumber());

        if (optionalUser.isEmpty())
            throw new UserNotFoundException("User does not exist");

        return optionalUser.get();
    }

    private BCryptPasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder(bcryptStrength);
    }

    @Transactional
    public void updateUserPassword(String newPassword, User user) {
        user.setPasswordHash(getEncoder().encode(newPassword));
        userRepo.save(user);
    }

    public User resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = resetTokenService.getPasswordResetToken(request.resetPasswordToken());
        User user = resetToken.getUser();
        resetTokenService.markTokenAsUsed(resetToken);
        this.updateUserPassword(request.newPassword(), user);
        return user;
    }
}
