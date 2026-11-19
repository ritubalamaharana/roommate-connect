package com.roommateconnect.service;

import com.roommateconnect.dto.AuthResponse;
import com.roommateconnect.dto.LoginRequest;
import com.roommateconnect.dto.RegisterRequest;
import com.roommateconnect.dto.UserResponse;
import com.roommateconnect.entity.AccountStatus;
import com.roommateconnect.entity.Role;
import com.roommateconnect.entity.User;
import com.roommateconnect.exception.BadRequestException;
import com.roommateconnect.exception.ConflictException;
import com.roommateconnect.exception.ForbiddenException;
import com.roommateconnect.exception.UnauthorizedException;
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
    private final JwtService jwtService;

    @Transactional
    public UserResponse register(RegisterRequest request) {
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
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Email is already registered");
        }

        log.info("New user registered with id={}", user.getId());
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (user.getAccountStatus() == AccountStatus.SUSPENDED) {
            throw new ForbiddenException("This account is suspended");
        }

        String token = jwtService.generateAccessToken(user);
        log.info("User id={} logged in", user.getId());

        return new AuthResponse(token, "Bearer", jwtService.getAccessTokenSeconds(), UserResponse.from(user));
    }
}