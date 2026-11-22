package com.axlero.dashboardbuilder.services;

import com.axlero.dashboardbuilder.dto.request.MetricRequest;
import com.axlero.dashboardbuilder.dto.response.MetricResponse;
import com.axlero.dashboardbuilder.enums.WorkspaceRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Ad-hoc metric queries (used by the dashboard builder's live preview).
 * Authorisation happens here, before the engine runs, so a cached result can never skip the check.
 */
@Service
@RequiredArgsConstructor
public class MetricService {

    private final WorkspaceAccessService access;
    private final MetricQueryExecutor executor;

    public MetricResponse query(String email, UUID workspaceId, MetricRequest request) {
        access.requireRole(workspaceId, email, WorkspaceRole.VIEWER);
        return executor.execute(workspaceId, request);
    }
}
