package com.roommateconnect.service;

import com.roommateconnect.dto.HealthStatusResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class HealthService {

    private static final Logger log = LoggerFactory.getLogger(HealthService.class);

    private final DataSource dataSource;

    public HealthStatusResponse check() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                return new HealthStatusResponse("UP", "UP", connection.getCatalog(), Instant.now());
            }
            return new HealthStatusResponse("DEGRADED", "DOWN", null, Instant.now());
        } catch (SQLException ex) {
            log.error("Database health check failed", ex);
            return new HealthStatusResponse("DEGRADED", "DOWN", null, Instant.now());
        }
    }
}