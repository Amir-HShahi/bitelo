package dev.burgerman.bitelo.services;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

import dev.burgerman.bitelo.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class Google2FAService {
    private final String ISSUER = "Bitelo"; // TODO
    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    public String generateSecretKey(User user) {
        log.info("Generating new google authenticator secret code for user");
        final GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    public String generateSetupKey(User user) {
        log.info("Generating new google authenticator URL for user: {}", user.getId().toString());
        GoogleAuthenticatorKey secretKey = new GoogleAuthenticatorKey.Builder(
                user.getGoogle2FASecretCode()).build();

        return GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(
                this.ISSUER,
                user.getPhoneNumber(),
                secretKey);
    }

    public boolean verifyCode(User user, String code) {
        String userId = user.getId().toString();
        log.info("2FA verification attempt for user: {}", userId);
        try {
            log.info("Authorizing user {} google 2FA code", userId);
            int intCode = Integer.parseInt(code);
            return gAuth.authorize(user.getGoogle2FASecretCode(), intCode);
        } catch (NumberFormatException e) {
            log.warn("2FA verification failed for user: {} - invalid format", userId);
            throw new BadCredentialsException("Invalid code provided");
        }
    }
}
