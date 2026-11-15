package com.axlero.dashboardbuilder.auth.dto;

import com.axlero.dashboardbuilder.user.entity.Role;

import java.util.UUID;

public record UserResponse(UUID id, String email, String fullName, Role role) {
}