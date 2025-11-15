package dev.burgerman.bitelo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.burgerman.bitelo.model.User;
import dev.burgerman.bitelo.model.dto.LoginRequest;
import dev.burgerman.bitelo.model.dto.RegistrationRequest;
import dev.burgerman.bitelo.model.exception.InvalidCredentialsException;
import dev.burgerman.bitelo.model.exception.UnverifiedUserException;
import dev.burgerman.bitelo.model.exception.UserAlreadyExistsException;
import dev.burgerman.bitelo.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    @Value("${app.security.bcrypt.strength}")
    private int bcryptStrength;

    private final UserRepo userRepo;
    private final Google2FAService google2faService;

    @Transactional
    public User registerUser(RegistrationRequest request) {
        ensureUserNotExists(request);
        log.info("Registering user with provided request");
        User user = new User();
        user.setPhoneCountryCode(request.countryCode());
        user.setPhoneNumber(request.phoneNumber());
        user.setPasswordHash(getEncoder().encode(request.password()));
        String secret = google2faService.generateSecretKey(user);
        user.setGoogle2FASecretCode(secret);
        return userRepo.save(user);
    }

    public User loginUser(LoginRequest request) {
        log.info("Finding user with provided phone to login");
        User user = userRepo.findByPhoneNumber(request.phoneNumber())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!getEncoder().matches(request.password(), user.getPasswordHash())) {
            log.warn("Provided password didn't match to linked user phone number");
            throw new InvalidCredentialsException("Invalid credentials");
        }

        if (!user.isVerified()) {
            log.warn("User {} login was successful but user has not been verified", user.getId());
            throw new UnverifiedUserException("Please verify your account first");
        }

        return user;
    }

    public void logoutUser() {
        // TODO
    }

    private void ensureUserNotExists(RegistrationRequest request) {
        log.info("Checking if user with phone exists");
        if (userRepo.existsByPhoneNumber(request.phoneNumber())) {
            log.warn("User with provided phone already exists!");
            throw new UserAlreadyExistsException(
                    "An account with this phone number already exists. Try logging in instead.");
        }
    }

    private BCryptPasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder(bcryptStrength);
    }
}
