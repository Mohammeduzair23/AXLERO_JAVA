package com.axlero.dashboardbuilder.controllers;

import com.axlero.dashboardbuilder.dto.request.MetricRequest;
import com.axlero.dashboardbuilder.dto.response.MetricResponse;
import com.axlero.dashboardbuilder.services.MetricService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/metrics")
@RequiredArgsConstructor
public class MetricController {

    private final MetricService metricService;

    /** POST because the query is a JSON body; it only reads data. */
    @PostMapping("/query")
    public MetricResponse query(@AuthenticationPrincipal UserDetails principal, @PathVariable UUID workspaceId,
                                @Valid @RequestBody MetricRequest request) {
        return metricService.query(principal.getUsername(), workspaceId, request);
    }
}