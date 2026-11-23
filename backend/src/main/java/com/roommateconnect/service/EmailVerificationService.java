package com.roommateconnect.service;

import com.roommateconnect.entity.AccountStatus;
import com.roommateconnect.entity.User;
import com.roommateconnect.entity.VerificationToken;
import com.roommateconnect.exception.BadRequestException;
import com.roommateconnect.repository.UserRepository;
import com.roommateconnect.repository.VerificationTokenRepository;
import com.roommateconnect.security.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Locale;

@Service
public class EmailVerificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailVerificationService.class);

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final long expiryHours;
    private final String frontendUrl;

    public EmailVerificationService(VerificationTokenRepository tokenRepository,
                                    UserRepository userRepository,
                                    @Value("${app.verification.expiry-hours}") long expiryHours,
                                    @Value("${app.frontend-url}") String frontendUrl) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.expiryHours = expiryHours;
        this.frontendUrl = frontendUrl;
    }

    @Transactional
    public void sendVerification(User user) {
        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setToken(TokenUtils.randomToken());
        token.setExpiresAt(LocalDateTime.now(ZoneOffset.UTC).plusHours(expiryHours));
        tokenRepository.save(token);

        log.info("[EMAIL STUB] Verification link for userId={}: {}/verify-email?token={}",
                user.getId(), frontendUrl, token.getToken());
    }

    @Transactional
    public void verify(String tokenValue) {
        VerificationToken token = tokenRepository.findByToken(tokenValue.trim())
                .orElseThrow(() -> new BadRequestException("Invalid or expired verification link"));

        if (token.isUsed() || token.isExpired()) {
            throw new BadRequestException("Invalid or expired verification link");
        }

        token.getUser().setEmailVerified(true);
        token.setUsed(true);
    }

    @Transactional
    public void resend(String email) {
        userRepository.findByEmail(email.trim().toLowerCase(Locale.ROOT))
                .filter(user -> !user.isEmailVerified() && user.getAccountStatus() == AccountStatus.ACTIVE)
                .ifPresent(this::sendVerification);
    }
}