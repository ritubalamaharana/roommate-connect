package com.roommateconnect.dto;

import com.roommateconnect.entity.LifestyleOptions;

public record PreferencesRequest(
        LifestyleOptions.Cleanliness cleanlinessPreference,
        LifestyleOptions.Noise noisePreference,
        LifestyleOptions.Social socialPreference,
        LifestyleOptions.Smoking smokingPreference,
        LifestyleOptions.Food foodPreference,
        LifestyleOptions.Sharing sharingPreference
) {
}