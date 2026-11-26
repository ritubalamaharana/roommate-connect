package com.roommateconnect.controller;

import com.roommateconnect.dto.ApiResponse;
import com.roommateconnect.dto.PreferencesRequest;
import com.roommateconnect.dto.PreferencesResponse;
import com.roommateconnect.dto.ProfileResponse;
import com.roommateconnect.dto.ProfileUpdateRequest;
import com.roommateconnect.service.PreferenceService;
import com.roommateconnect.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final PreferenceService preferenceService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.success("Profile", profileService.getMyProfile(userId)));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateMyProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ProfileUpdateRequest request) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(
                ApiResponse.success("Profile updated", profileService.updateMyProfile(userId, request)));
    }

    @GetMapping("/preferences")
    public ResponseEntity<ApiResponse<PreferencesResponse>> getMyPreferences(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(
                ApiResponse.success("Preferences", preferenceService.getMyPreferences(userId)));
    }

    @PutMapping("/preferences")
    public ResponseEntity<ApiResponse<PreferencesResponse>> updateMyPreferences(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody PreferencesRequest request) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(
                ApiResponse.success("Preferences updated", preferenceService.updateMyPreferences(userId, request)));
    }
}