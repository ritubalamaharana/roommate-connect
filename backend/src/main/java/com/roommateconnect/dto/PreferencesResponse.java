package com.roommateconnect.dto;

import com.roommateconnect.entity.LifestyleOptions;
import com.roommateconnect.entity.RoommatePreference;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

public record PreferencesResponse(
        LifestyleOptions.Cleanliness cleanlinessPreference,
        LifestyleOptions.Noise noisePreference,
        LifestyleOptions.Social socialPreference,
        LifestyleOptions.Smoking smokingPreference,
        LifestyleOptions.Food foodPreference,
        LifestyleOptions.Sharing sharingPreference,
        int answeredCount,
        LocalDateTime updatedAt) {

    public static PreferencesResponse from(RoommatePreference p) {
        long answered = Stream.of(
                        p.getCleanlinessPreference(), p.getNoisePreference(), p.getSocialPreference(),
                        p.getSmokingPreference(), p.getFoodPreference(), p.getSharingPreference())
                .filter(Objects::nonNull)
                .count();

        return new PreferencesResponse(
                p.getCleanlinessPreference(),
                p.getNoisePreference(),
                p.getSocialPreference(),
                p.getSmokingPreference(),
                p.getFoodPreference(),
                p.getSharingPreference(),
                (int) answered,
                p.getUpdatedAt());
    }

    public static PreferencesResponse empty() {
        return new PreferencesResponse(null, null, null, null, null, null, 0, null);
    }
}