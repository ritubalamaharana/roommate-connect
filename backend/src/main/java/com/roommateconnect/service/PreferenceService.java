package com.roommateconnect.service;

import com.roommateconnect.dto.PreferencesRequest;
import com.roommateconnect.dto.PreferencesResponse;
import com.roommateconnect.entity.RoommatePreference;
import com.roommateconnect.entity.User;
import com.roommateconnect.exception.ConflictException;
import com.roommateconnect.repository.RoommatePreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PreferenceService {

    private final ActiveUserService activeUserService;
    private final RoommatePreferenceRepository preferenceRepository;

    @Transactional(readOnly = true)
    public PreferencesResponse getMyPreferences(Long userId) {
        activeUserService.loadActiveUser(userId);
        return preferenceRepository.findByUserId(userId)
                .map(PreferencesResponse::from)
                .orElseGet(PreferencesResponse::empty);
    }

    @Transactional
    public PreferencesResponse updateMyPreferences(Long userId, PreferencesRequest request) {
        User user = activeUserService.loadActiveUser(userId);

        RoommatePreference preference = preferenceRepository.findByUserId(userId).orElseGet(() -> {
            RoommatePreference created = new RoommatePreference();
            created.setUser(user);
            return created;
        });

        preference.setCleanlinessPreference(request.cleanlinessPreference());
        preference.setNoisePreference(request.noisePreference());
        preference.setSocialPreference(request.socialPreference());
        preference.setSmokingPreference(request.smokingPreference());
        preference.setFoodPreference(request.foodPreference());
        preference.setSharingPreference(request.sharingPreference());

        try {
            preferenceRepository.saveAndFlush(preference);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Your preferences were saved at the same moment. Please try again");
        }

        return PreferencesResponse.from(preference);
    }
}