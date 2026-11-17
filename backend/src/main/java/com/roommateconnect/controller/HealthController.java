package com.roommateconnect.controller;

import com.roommateconnect.dto.ApiResponse;
import com.roommateconnect.dto.HealthStatusResponse;
import com.roommateconnect.service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
public class HealthController {

    private final HealthService healthService;

    @GetMapping
    public ResponseEntity<ApiResponse<HealthStatusResponse>> health() {
        HealthStatusResponse result = healthService.check();
        boolean up = "UP".equals(result.status());

        ApiResponse<HealthStatusResponse> body = up
                ? ApiResponse.success("Service is running", result)
                : ApiResponse.error("Database is unreachable", result);

        return ResponseEntity
                .status(up ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE)
                .body(body);
    }
}