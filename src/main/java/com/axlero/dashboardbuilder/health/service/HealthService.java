package com.axlero.dashboardbuilder.health.service;

import com.axlero.dashboardbuilder.health.dto.HealthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class HealthService {

    private final String serviceName;

    public HealthService(@Value("${spring.application.name}") String serviceName) {
        this.serviceName = serviceName;
    }

    public HealthResponse status() {
        return new HealthResponse("UP", serviceName, Instant.now());
    }
}
