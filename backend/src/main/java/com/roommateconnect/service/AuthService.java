package com.roommateconnect.service;

import com.roommateconnect.dto.RegisterRequest;
import com.roommateconnect.dto.UserResponse;
import com.roommateconnect.entity.Role;
import com.roommateconnect.entity.User;
import com.roommateconnect.exception.BadRequestException;
import com.roommateconnect.exception.ConflictException;
import com.roommateconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        // "Test@Mail.com" and "test@mail.com" must be the same account
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        Role role = request.role() == null ? Role.SEEKER : request.role();
        if (role == Role.ADMIN) {
            throw new BadRequestException("This role cannot be selected at registration");
        }

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email is already registered");
        }

        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.addRole(role);

        try {
            // saveAndFlush sends the INSERT now, so a duplicate is caught right here
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            // Two people registering the same email at the same moment: the database unique key wins
            throw new ConflictException("Email is already registered");
        }

        log.info("New user registered with id={}", user.getId());
        return UserResponse.from(user);
    }
}