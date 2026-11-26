package com.roommateconnect.dto;

import com.roommateconnect.entity.AccommodationType;
import com.roommateconnect.entity.Occupation;
import com.roommateconnect.entity.ProfileVisibility;
import com.roommateconnect.entity.User;
import com.roommateconnect.entity.UserProfile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProfileResponse(
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
        ProfileVisibility profileVisibility,
        boolean showInRecommendations,
        boolean profileComplete,
        LocalDateTime updatedAt) {

    public static ProfileResponse from(User user, UserProfile profile) {
        return new ProfileResponse(
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
                profile.getProfileVisibility(),
                profile.isShowInRecommendations(),
                profile.isCompleteForMatching(),
                profile.getUpdatedAt());
    }
}