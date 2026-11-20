package com.axlero.dashboardbuilder.services;

import com.axlero.dashboardbuilder.dto.request.DashboardRequest;
import com.axlero.dashboardbuilder.dto.response.DashboardResponse;
import com.axlero.dashboardbuilder.entities.Dashboard;
import com.axlero.dashboardbuilder.entities.WorkspaceMember;
import com.axlero.dashboardbuilder.enums.WorkspaceRole;
import com.axlero.dashboardbuilder.exceptions.ResourceNotFoundException;
import com.axlero.dashboardbuilder.repositories.DashboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/** Viewers can read; editors and owners can create, change and delete. */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final WorkspaceAccessService access;

    @Transactional
    public DashboardResponse create(String email, UUID workspaceId, DashboardRequest request) {
        WorkspaceMember member = access.requireRole(workspaceId, email, WorkspaceRole.EDITOR);
        Dashboard dashboard = new Dashboard();
        dashboard.setWorkspace(member.getWorkspace());
        apply(dashboard, request);
        dashboardRepository.save(dashboard);
        log.info("Dashboard created: id={} workspace={}", dashboard.getId(), workspaceId);
        return toResponse(dashboard);
    }

    @Transactional(readOnly = true)
    public List<DashboardResponse> list(String email, UUID workspaceId) {
        access.requireRole(workspaceId, email, WorkspaceRole.VIEWER);
        return dashboardRepository.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DashboardResponse get(String email, UUID workspaceId, UUID dashboardId) {
        access.requireRole(workspaceId, email, WorkspaceRole.VIEWER);
        return toResponse(find(workspaceId, dashboardId));
    }

    @Transactional
    public DashboardResponse update(String email, UUID workspaceId, UUID dashboardId, DashboardRequest request) {
        access.requireRole(workspaceId, email, WorkspaceRole.EDITOR);
        Dashboard dashboard = find(workspaceId, dashboardId);
        apply(dashboard, request);
        log.info("Dashboard updated: id={}", dashboardId);
        return toResponse(dashboard);
    }

    @Transactional
    public void delete(String email, UUID workspaceId, UUID dashboardId) {
        access.requireRole(workspaceId, email, WorkspaceRole.EDITOR);
        dashboardRepository.delete(find(workspaceId, dashboardId));
        log.info("Dashboard deleted: id={}", dashboardId);
    }

    private Dashboard find(UUID workspaceId, UUID dashboardId) {
        return dashboardRepository.findByIdAndWorkspaceId(dashboardId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Dashboard", dashboardId));
    }

    private void apply(Dashboard dashboard, DashboardRequest request) {
        dashboard.setName(request.name().trim());
        dashboard.setDescription(request.description());
    }

    private DashboardResponse toResponse(Dashboard d) {
        return new DashboardResponse(d.getId(), d.getWorkspace().getId(), d.getName(), d.getDescription(),
                d.getCreatedAt(), d.getUpdatedAt());
    }
}