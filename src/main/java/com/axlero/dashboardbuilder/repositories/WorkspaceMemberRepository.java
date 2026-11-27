package com.axlero.dashboardbuilder.repositories;

import com.axlero.dashboardbuilder.entities.WorkspaceMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, UUID> {

    /** The caller's membership; the workspace is fetched in the same query. */
    @EntityGraph(attributePaths = "workspace")
    Optional<WorkspaceMember> findByWorkspaceIdAndUserEmail(UUID workspaceId, String email);

    /** All workspaces a user belongs to, fetched in one query (no N+1). */
    @EntityGraph(attributePaths = "workspace")
    List<WorkspaceMember> findByUserEmailOrderByWorkspaceNameAsc(String email);

    @EntityGraph(attributePaths = "user")
    List<WorkspaceMember> findByWorkspaceIdOrderByCreatedAtAsc(UUID workspaceId);

    boolean existsByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);
    Optional<WorkspaceMember> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);
}