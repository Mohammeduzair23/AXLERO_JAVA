package com.axlero.dashboardbuilder.dto.response;

import com.axlero.dashboardbuilder.enums.WorkspaceRole;

import java.time.LocalDateTime;
import java.util.UUID;

/** {@code role} is the calling user's role in this workspace. */
public record WorkspaceResponse(UUID id, String name, WorkspaceRole role, LocalDateTime createdAt) {
}
