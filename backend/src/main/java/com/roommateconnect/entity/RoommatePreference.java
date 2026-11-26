package com.roommateconnect.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(
        name = "roommate_preferences",
        uniqueConstraints = @UniqueConstraint(name = "uk_preference_user", columnNames = "user_id")
)
@Getter
@Setter
@NoArgsConstructor
public class RoommatePreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "cleanliness_preference", length = 30)
    private LifestyleOptions.Cleanliness cleanlinessPreference;

    @Enumerated(EnumType.STRING)
    @Column(name = "noise_preference", length = 30)
    private LifestyleOptions.Noise noisePreference;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_preference", length = 30)
    private LifestyleOptions.Social socialPreference;

    @Enumerated(EnumType.STRING)
    @Column(name = "smoking_preference", length = 30)
    private LifestyleOptions.Smoking smokingPreference;

    @Enumerated(EnumType.STRING)
    @Column(name = "food_preference", length = 30)
    private LifestyleOptions.Food foodPreference;

    @Enumerated(EnumType.STRING)
    @Column(name = "sharing_preference", length = 30)
    private LifestyleOptions.Sharing sharingPreference;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    void touch() {
        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }
}