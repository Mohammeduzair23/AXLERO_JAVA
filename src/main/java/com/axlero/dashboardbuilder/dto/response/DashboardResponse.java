package com.axlero.dashboardbuilder.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record DashboardResponse(UUID id, UUID workspaceId, String name, String description,
                                LocalDateTime createdAt, LocalDateTime updatdAt) {
}
