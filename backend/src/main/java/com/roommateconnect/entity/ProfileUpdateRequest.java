package com.roommateconnect.dto;

import com.roommateconnect.entity.AccommodationType;
import com.roommateconnect.entity.Occupation;
import com.roommateconnect.entity.ProfileVisibility;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProfileUpdateRequest(

        Occupation occupation,

        @Size(max = 1000, message = "Bio must be at most 1000 characters")
        String bio,

        @Size(max = 100, message = "City must be at most 100 characters")
        String preferredCity,

        @Size(max = 150, message = "Locality must be at most 150 characters")
        String preferredLocality,

        @DecimalMin(value = "0.00", message = "Minimum budget cannot be negative")
        @Digits(integer = 8, fraction = 2, message = "Minimum budget: at most 8 digits and 2 decimals")
        BigDecimal monthlyBudgetMin,

        @DecimalMin(value = "0.00", message = "Maximum budget cannot be negative")
        @Digits(integer = 8, fraction = 2, message = "Maximum budget: at most 8 digits and 2 decimals")
        BigDecimal monthlyBudgetMax,

        LocalDate moveInDate,

        AccommodationType accommodationType,

        ProfileVisibility profileVisibility,

        Boolean showInRecommendations
) {
}