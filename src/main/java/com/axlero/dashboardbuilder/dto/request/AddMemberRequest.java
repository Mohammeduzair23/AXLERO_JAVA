package com.axlero.dashboardbuilder.dto.request;

import com.axlero.dashboardbuilder.enums.WorkspaceRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddMemberRequest(
        @NotBlank @Email String email,
        @NotNull  WorkspaceRole role
) {
}
