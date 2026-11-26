package com.roommateconnect.service;

import com.roommateconnect.dto.PreferencesResponse;
import com.roommateconnect.dto.PublicProfileResponse;
import com.roommateconnect.entity.AccountStatus;
import com.roommateconnect.entity.ProfileVisibility;
import com.roommateconnect.entity.User;
import com.roommateconnect.entity.UserProfile;
import com.roommateconnect.exception.NotFoundException;
import com.roommateconnect.repository.RoommatePreferenceRepository;
import com.roommateconnect.repository.UserProfileRepository;
import com.roommateconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileViewService {

    private static final String NOT_FOUND_MESSAGE = "Profile not found";

    private final ActiveUserService activeUserService;
    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final RoommatePreferenceRepository preferenceRepository;

    @Transactional(readOnly = true)
    public PublicProfileResponse viewProfile(Long viewerId, Long targetUserId) {
        activeUserService.loadActiveUser(viewerId);

        User target = userRepository.findById(targetUserId)
                .filter(user -> user.getAccountStatus() == AccountStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

        UserProfile profile = profileRepository.findByUserId(targetUserId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

        boolean isOwner = viewerId.equals(targetUserId);
        if (!isOwner && !visibleToOtherMembers(profile)) {
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }

        PreferencesResponse preferences = preferenceRepository.findByUserId(targetUserId)
                .map(PreferencesResponse::from)
                .orElseGet(PreferencesResponse::empty);

        return PublicProfileResponse.from(target, profile, preferences);
    }

    private boolean visibleToOtherMembers(UserProfile profile) {
        return profile.getProfileVisibility() == ProfileVisibility.MEMBERS;
    }
}