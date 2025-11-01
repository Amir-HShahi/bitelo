package dev.burgerman.bitelo.services;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dev.burgerman.bitelo.model.User;
import dev.burgerman.bitelo.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Finding a user with username: {}", username);
        Optional<User> optionalUser = userRepo.findByPhoneNumber(username);

        if (optionalUser.isEmpty()) {
            log.warn("Couldn't find a user with username: {}", username);
            throw new UsernameNotFoundException(username);
        }

        return optionalUser.get();
    }
}
