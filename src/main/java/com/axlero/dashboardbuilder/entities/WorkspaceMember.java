package com.axlero.dashboardbuilder.entities;

import com.axlero.dashboardbuilder.enums.WorkspaceRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/** Links a user to a workspace with a role. This is what makes the platform multi-tenant. */
@Entity
@Table(name = "workspace_members",
        uniqueConstraints = @UniqueConstraint(name = "uk_workspace_member", columnNames = {"workspace_id", "user_id"}),
        indexes = @Index(name = "idx_members_user", columnList = "user_id"))
@Getter
@Setter
@NoArgsConstructor
public class WorkspaceMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkspaceRole role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public WorkspaceMember(Workspace workspace, User user, WorkspaceRole role) {
        this.workspace = workspace;
        this.user = user;
        this.role = role;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}