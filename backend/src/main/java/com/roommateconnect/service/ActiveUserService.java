package com.roommateconnect.service;

import com.roommateconnect.entity.AccountStatus;
import com.roommateconnect.entity.User;
import com.roommateconnect.exception.ForbiddenException;
import com.roommateconnect.exception.UnauthorizedException;
import com.roommateconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActiveUserService {

    private final UserRepository userRepository;

    public User loadActiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User no longer exists"));
        if (user.getAccountStatus() == AccountStatus.SUSPENDED) {
            throw new ForbiddenException("This account is suspended");
        }
        return user;
    }
}