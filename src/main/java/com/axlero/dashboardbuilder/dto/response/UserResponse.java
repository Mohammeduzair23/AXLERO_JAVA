package com.axlero.dashboardbuilder.dto.response;

import com.axlero.dashboardbuilder.enums.Role;

import java.util.UUID;

public record UserResponse(UUID id, String email, String fullName, Role role) {
}