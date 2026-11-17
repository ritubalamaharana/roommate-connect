package com.roommateconnect.dto;

import java.time.Instant;

public record HealthStatusResponse(
        String status,
        String database,
        String databaseName,
        Instant checkedAt) {
}