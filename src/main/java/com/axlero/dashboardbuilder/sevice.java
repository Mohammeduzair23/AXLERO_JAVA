package com.axlero.dashboard.service;

import com.axlero.dashboard.domain.Dashboard;
import com.axlero.dashboard.exception.NotFoundException;
import com.axlero.dashboard.repository.DashboardRepository;
import com.axlero.dashboard.repository.WorkspaceRepository;
import com.axlero.dashboard.security.TenantContext;
import com.axlero.dashboard.web.dto.DashboardDtos.CreateDashboardRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DashboardService {

    private final DashboardRepository dashboards;
    private final WorkspaceRepository workspaces;

    public DashboardService(DashboardRepository dashboards, WorkspaceRepository workspaces) {
        this.dashboards = dashboards;
        this.workspaces = workspaces;
    }

    @Transactional(readOnly = true)
    public List<Dashboard> list() {
        return dashboards.findByWorkspace_IdOrderByUpdatedAtDesc(TenantContext.workspaceId());
    }

    @Transactional(readOnly = true)
    public Dashboard get(UUID id) {
      
        return dashboards.findByIdAndWorkspace_Id(id, TenantContext.workspaceId())
                .orElseThrow(() -> new NotFoundException("Dashboard"));
    }

    public Dashboard create(CreateDashboardRequest req) {
        Dashboard d = new Dashboard();
        d.setWorkspace(workspaces.getReferenceById(TenantContext.workspaceId()));  // from JWT, never from the request body
        d.setCreatedBy(TenantContext.userId());
        d.setName(req.name());
        d.setDescription(req.description());
        return dashboards.save(d);
    }

    public void delete(UUID id) {
        dashboards.delete(get(id));
    }
}
