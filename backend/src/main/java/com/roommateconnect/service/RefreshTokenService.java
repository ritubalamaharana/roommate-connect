package com.roommateconnect.service;

import com.roommateconnect.entity.AccountStatus;
import com.roommateconnect.entity.RefreshToken;
import com.roommateconnect.entity.User;
import com.roommateconnect.exception.ForbiddenException;
import com.roommateconnect.exception.UnauthorizedException;
import com.roommateconnect.repository.RefreshTokenRepository;
import com.roommateconnect.repository.UserRepository;
import com.roommateconnect.security.TokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class RefreshTokenService {

    public record RotatedToken(User user, String refreshToken) {
    }

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final long refreshTokenDays;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               UserRepository userRepository,
                               @Value("${app.jwt.refresh-token-days}") long refreshTokenDays) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.refreshTokenDays = refreshTokenDays;
    }

    @Transactional
    public String issue(User user) {
        String rawToken = TokenUtils.randomToken();

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setTokenHash(TokenUtils.sha256Hex(rawToken));
        token.setExpiresAt(LocalDateTime.now(ZoneOffset.UTC).plusDays(refreshTokenDays));
        refreshTokenRepository.save(token);

        return rawToken;
    }

    @Transactional(noRollbackFor = UnauthorizedException.class)
    public RotatedToken rotate(String rawToken) {
        RefreshToken stored = refreshTokenRepository.findByTokenHash(TokenUtils.sha256Hex(rawToken))
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        Long userId = stored.getUser().getId();

        if (stored.isRevoked()) {
            refreshTokenRepository.revokeAllForUser(userId);
            throw new UnauthorizedException("Refresh token is no longer valid");
        }
        if (stored.isExpired()) {
            throw new UnauthorizedException("Refresh token has expired");
        }

        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
        if (user.getAccountStatus() == AccountStatus.SUSPENDED) {
            throw new ForbiddenException("This account is suspended");
        }

        stored.setRevoked(true);
        return new RotatedToken(user, issue(user));
    }

    @Transactional
    public void revoke(String rawToken) {
        refreshTokenRepository.findByTokenHash(TokenUtils.sha256Hex(rawToken))
                .ifPresent(token -> token.setRevoked(true));
    }
}