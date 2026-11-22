package com.axlero.dashboardbuilder.dto.response;

import com.axlero.dashboardbuilder.enums.WorkspaceRole;

import java.util.UUID;

public record MemberResponse(UUID userid, String email, String fullName, WorkspaceRole role) {
}
