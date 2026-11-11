package com.axlero.dashboardbuilder.common.controller;

import com.axlero.dashboardbuilder.common.response.StatusResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/** Simple public endpoint to confirm the API is reachable. */
@RestController
@RequestMapping("/api")
public class StatusController {

    private final String applicationName;

    public StatusController(@Value("${spring.application.name}") String applicationName) {
        this.applicationName = applicationName;
    }

    @GetMapping("/status")
    public StatusResponse status() {
        return new StatusResponse(applicationName, "UP", Instant.now());
    }
}