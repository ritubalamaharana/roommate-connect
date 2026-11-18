package com.roommateconnect.dto;

import com.roommateconnect.entity.User;

import java.time.LocalDateTime;
import java.util.List;

/** What the API returns about a user. Never includes the password hash. */
public record UserResponse(
        Long id,
        String fullName,
        String email,
        List<String> roles,
        boolean emailVerified,
        LocalDateTime createdAt) {

    public static UserResponse from(User user) {
        List<String> roles = user.getRoles().stream()
                .map(userRole -> userRole.getRole().name())
                .sorted()
                .toList();

        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                roles,
                user.isEmailVerified(),
                user.getCreatedAt());
    }
}