package com.roommateconnect.service;

import com.roommateconnect.dto.ProfileResponse;
import com.roommateconnect.dto.ProfileUpdateRequest;
import com.roommateconnect.entity.User;
import com.roommateconnect.entity.UserProfile;
import com.roommateconnect.exception.BadRequestException;
import com.roommateconnect.exception.ConflictException;
import com.roommateconnect.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ActiveUserService activeUserService;
    private final UserProfileRepository profileRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile(Long userId) {
        User user = activeUserService.loadActiveUser(userId);
        // No profile yet: show an empty one (it is only saved on the first PUT)
        UserProfile profile = profileRepository.findByUserId(userId).orElseGet(UserProfile::new);
        return ProfileResponse.from(user, profile);
    }

    @Transactional
    public ProfileResponse updateMyProfile(Long userId, ProfileUpdateRequest request) {
        User user = activeUserService.loadActiveUser(userId);
        validateBusinessRules(request);

        UserProfile profile = profileRepository.findByUserId(userId).orElseGet(() -> {
            UserProfile created = new UserProfile();
            created.setUser(user);
            return created;
        });

        profile.setOccupation(request.occupation());
        profile.setBio(clean(request.bio()));
        profile.setPreferredCity(clean(request.preferredCity()));
        profile.setPreferredLocality(clean(request.preferredLocality()));
        profile.setMonthlyBudgetMin(request.monthlyBudgetMin());
        profile.setMonthlyBudgetMax(request.monthlyBudgetMax());
        profile.setMoveInDate(request.moveInDate());
        profile.setAccommodationType(request.accommodationType());

        if (request.profileVisibility() != null) {
            profile.setProfileVisibility(request.profileVisibility());
        }
        if (request.showInRecommendations() != null) {
            profile.setShowInRecommendations(request.showInRecommendations());
        }

        try {
            profileRepository.saveAndFlush(profile);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Your profile was saved at the same moment. Please try again");
        }

        return ProfileResponse.from(user, profile);
    }

    private void validateBusinessRules(ProfileUpdateRequest request) {
        boolean hasMin = request.monthlyBudgetMin() != null;
        boolean hasMax = request.monthlyBudgetMax() != null;

        if (hasMin != hasMax) {
            throw new BadRequestException("Provide both minimum and maximum budget, or neither");
        }
        if (hasMin && request.monthlyBudgetMin().compareTo(request.monthlyBudgetMax()) > 0) {
            throw new BadRequestException("Minimum budget cannot be more than maximum budget");
        }

        LocalDate earliest = LocalDate.now(ZoneOffset.UTC).minusDays(1);
        if (request.moveInDate() != null && request.moveInDate().isBefore(earliest)) {
            throw new BadRequestException("Move-in date cannot be in the past");
        }
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}