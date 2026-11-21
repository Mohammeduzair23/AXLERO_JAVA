package com.axlero.dashboardbuilder.repositories;

import com.axlero.dashboardbuilder.entities.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
}
