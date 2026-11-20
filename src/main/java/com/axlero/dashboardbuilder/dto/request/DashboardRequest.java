package com.axlero.dashboardbuilder.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DashboardRequest(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 500) String description
) {
}
