package com.roommateconnect.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(
        name = "user_profiles",
        uniqueConstraints = @UniqueConstraint(name = "uk_profile_user", columnNames = "user_id"),
        indexes = @Index(name = "idx_profile_city_locality", columnList = "preferred_city, preferred_locality")
)
@Getter
@Setter
@NoArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(length = 40)
    private Occupation occupation;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "preferred_city", length = 100)
    private String preferredCity;

    @Column(name = "preferred_locality", length = 150)
    private String preferredLocality;

    @Column(name = "monthly_budget_min", precision = 10, scale = 2)
    private BigDecimal monthlyBudgetMin;

    @Column(name = "monthly_budget_max", precision = 10, scale = 2)
    private BigDecimal monthlyBudgetMax;

    @Column(name = "move_in_date")
    private LocalDate moveInDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "accommodation_type", length = 40)
    private AccommodationType accommodationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_visibility", nullable = false, length = 30)
    private ProfileVisibility profileVisibility = ProfileVisibility.MEMBERS;

    @Column(name = "show_in_recommendations", nullable = false)
    private boolean showInRecommendations = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    public boolean isCompleteForMatching() {
        return occupation != null
                && preferredCity != null && !preferredCity.isBlank()
                && monthlyBudgetMin != null && monthlyBudgetMax != null
                && moveInDate != null
                && accommodationType != null;
    }
}