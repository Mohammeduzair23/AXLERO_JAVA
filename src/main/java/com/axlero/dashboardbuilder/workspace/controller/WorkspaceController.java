package com.axlero.dashboardbuilder.workspace.controller;

import com.axlero.dashboardbuilder.workspace.dto.WorkspaceRequest;
import com.axlero.dashboardbuilder.workspace.dto.WorkspaceResponse;
import com.axlero.dashboardbuilder.workspace.service.WorkspaceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkspaceResponse createWorkspace(
            @Valid @RequestBody WorkspaceRequest request,
            @AuthenticationPrincipal UserDetails principal) {

        return workspaceService.createWorkspace(
                request,
                principal.getUsername()
        );
    }
}
