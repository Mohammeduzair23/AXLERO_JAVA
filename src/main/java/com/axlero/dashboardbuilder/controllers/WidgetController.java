package com.axlero.dashboardbuilder.controllers;
import java.time.LocalDate;
import com.axlero.dashboardbuilder.dto.request.WidgetRequest;
import com.axlero.dashboardbuilder.dto.response.MetricResponse;
import com.axlero.dashboardbuilder.dto.response.WidgetResponse;
import com.axlero.dashboardbuilder.services.WidgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/dashboards/{dashboardId}/widgets")
@RequiredArgsConstructor
public class WidgetController {

    private final WidgetService widgetService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WidgetResponse create(@AuthenticationPrincipal UserDetails principal, @PathVariable UUID workspaceId,
                                 @PathVariable UUID dashboardId, @Valid @RequestBody WidgetRequest request) {
        return widgetService.create(principal.getUsername(), workspaceId, dashboardId, request);
    }

    @GetMapping
    public List<WidgetResponse> list(@AuthenticationPrincipal UserDetails principal, @PathVariable UUID workspaceId,
                                     @PathVariable UUID dashboardId) {
        return widgetService.list(principal.getUsername(), workspaceId, dashboardId);
    }

    @GetMapping("/{widgetId}/data")
    public MetricResponse data(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable UUID workspaceId,
            @PathVariable UUID dashboardId,
            @PathVariable UUID widgetId,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo) {

        return widgetService.getData(
                principal.getUsername(),
                workspaceId,
                dashboardId,
                widgetId,
                dateFrom,
                dateTo
        );
    }

    @PutMapping("/{widgetId}")
    public WidgetResponse update(@AuthenticationPrincipal UserDetails principal, @PathVariable UUID workspaceId,
                                 @PathVariable UUID dashboardId, @PathVariable UUID widgetId,
                                 @Valid @RequestBody WidgetRequest request) {
        return widgetService.update(principal.getUsername(), workspaceId, dashboardId, widgetId, request);
    }

    @DeleteMapping("/{widgetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal UserDetails principal, @PathVariable UUID workspaceId,
                       @PathVariable UUID dashboardId, @PathVariable UUID widgetId) {
        widgetService.delete(principal.getUsername(), workspaceId, dashboardId, widgetId);
    }
}