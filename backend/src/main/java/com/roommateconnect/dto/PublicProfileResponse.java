package com.roommateconnect.dto;

import com.roommateconnect.entity.AccommodationType;
import com.roommateconnect.entity.Occupation;
import com.roommateconnect.entity.User;
import com.roommateconnect.entity.UserProfile;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PublicProfileResponse(
        Long userId,
        String fullName,
        Occupation occupation,
        String bio,
        String preferredCity,
        String preferredLocality,
        BigDecimal monthlyBudgetMin,
        BigDecimal monthlyBudgetMax,
        LocalDate moveInDate,
        AccommodationType accommodationType,
        PreferencesResponse preferences) {

    public static PublicProfileResponse from(User user, UserProfile profile, PreferencesResponse preferences) {
        return new PublicProfileResponse(
                user.getId(),
                user.getFullName(),
                profile.getOccupation(),
                profile.getBio(),
                profile.getPreferredCity(),
                profile.getPreferredLocality(),
                profile.getMonthlyBudgetMin(),
                profile.getMonthlyBudgetMax(),
                profile.getMoveInDate(),
                profile.getAccommodationType(),
                preferences);
    }
}