package com.axlero.dashboardbuilder.controllers;

import com.axlero.dashboardbuilder.dto.request.DashboardRequest;
import com.axlero.dashboardbuilder.dto.response.DashboardResponse;
import com.axlero.dashboardbuilder.services.DashboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/dashboards")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DashboardResponse create(@AuthenticationPrincipal UserDetails principal, @PathVariable UUID workspaceId,
                                    @Valid @RequestBody DashboardRequest request) {
        return dashboardService.create(principal.getUsername(), workspaceId, request);
    }

    @GetMapping
    public List<DashboardResponse> list(@AuthenticationPrincipal UserDetails principal,
                                        @PathVariable UUID workspaceId) {
        return dashboardService.list(principal.getUsername(), workspaceId);
    }

    @GetMapping("/{dashboardId}")
    public DashboardResponse get(@AuthenticationPrincipal UserDetails principal, @PathVariable UUID workspaceId,
                                 @PathVariable UUID dashboardId) {
        return dashboardService.get(principal.getUsername(), workspaceId, dashboardId);
    }

    @PutMapping("/{dashboardId}")
    public DashboardResponse update(@AuthenticationPrincipal UserDetails principal, @PathVariable UUID workspaceId,
                                    @PathVariable UUID dashboardId, @Valid @RequestBody DashboardRequest request) {
        return dashboardService.update(principal.getUsername(), workspaceId, dashboardId, request);
    }

    @DeleteMapping("/{dashboardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal UserDetails principal, @PathVariable UUID workspaceId,
                       @PathVariable UUID dashboardId) {
        dashboardService.delete(principal.getUsername(), workspaceId, dashboardId);
    }
}