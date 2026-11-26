package com.axlero.dashboardbuilder.controllers;

import com.axlero.dashboardbuilder.dto.request.AddMemberRequest;
import com.axlero.dashboardbuilder.dto.request.WorkspaceRequest;
import com.axlero.dashboardbuilder.dto.response.MemberResponse;
import com.axlero.dashboardbuilder.dto.response.WorkspaceResponse;
import com.axlero.dashboardbuilder.services.WorkspaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkspaceResponse create(@AuthenticationPrincipal UserDetails principal,
                                    @Valid @RequestBody WorkspaceRequest request) {
        return workspaceService.create(principal.getUsername(), request);
    }

    @GetMapping
    public List<WorkspaceResponse> list(@AuthenticationPrincipal UserDetails principal) {
        return workspaceService.listMine(principal.getUsername());
    }

    @GetMapping("/{workspaceId}")
    public WorkspaceResponse get(@AuthenticationPrincipal UserDetails principal, @PathVariable UUID workspaceId) {
        return workspaceService.get(principal.getUsername(), workspaceId);
    }

    @PutMapping("/{workspaceId}")
    public WorkspaceResponse rename(@AuthenticationPrincipal UserDetails principal, @PathVariable UUID workspaceId,
                                    @Valid @RequestBody WorkspaceRequest request) {
        return workspaceService.rename(principal.getUsername(), workspaceId, request);
    }

    @DeleteMapping("/{workspaceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal UserDetails principal, @PathVariable UUID workspaceId) {
        workspaceService.delete(principal.getUsername(), workspaceId);
    }

    @GetMapping("/{workspaceId}/members")
    public List<MemberResponse> members(@AuthenticationPrincipal UserDetails principal,
                                        @PathVariable UUID workspaceId) {
        return workspaceService.listMembers(principal.getUsername(), workspaceId);
    }

    @PostMapping("/{workspaceId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse addMember(@AuthenticationPrincipal UserDetails principal, @PathVariable UUID workspaceId,
                                    @Valid @RequestBody AddMemberRequest request) {
        return workspaceService.addMember(principal.getUsername(), workspaceId, request);
    }
    @DeleteMapping("/{workspaceId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable UUID workspaceId,
            @PathVariable UUID userId) {

        workspaceService.removeMember(
                principal.getUsername(),
                workspaceId,
                userId
        );
    }
}