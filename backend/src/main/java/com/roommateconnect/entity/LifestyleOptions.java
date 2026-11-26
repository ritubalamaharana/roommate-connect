package com.roommateconnect.entity;

public final class LifestyleOptions {

    private LifestyleOptions() {
    }

    public enum Cleanliness { RELAXED, MODERATE, VERY_TIDY }

    public enum Noise { QUIET, MODERATE, LIVELY }

    public enum Social { PREFER_PRIVACY, BALANCED, VERY_SOCIAL }

    public enum Smoking { NO_SMOKING, OUTSIDE_ONLY, SMOKING_OK }

    public enum Food { VEGETARIAN_KITCHEN, NON_VEG_ALLOWED, NO_PREFERENCE }

    public enum Sharing { OWN_SPACE, FLEXIBLE, HAPPY_TO_SHARE }
}