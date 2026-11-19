package com.axlero.dashboardbuilder.workspace.repository;

import com.axlero.dashboardbuilder.workspace.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
}
