package com.axlero.dashboardbuilder.repositories;

import com.axlero.dashboardbuilder.entities.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DashboardRepository extends JpaRepository<Dashboard, UUID> {

    List<Dashboard> findByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);

    /** Scoping by workspace is what stops one tenant reaching another tenant's dashboard by id. */
    Optional<Dashboard> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
}
