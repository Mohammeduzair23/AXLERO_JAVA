package com.axlero.dashboardbuilder.services;

import com.axlero.dashboardbuilder.dto.request.AddMemberRequest;
import com.axlero.dashboardbuilder.dto.request.WorkspaceRequest;
import com.axlero.dashboardbuilder.dto.response.MemberResponse;
import com.axlero.dashboardbuilder.dto.response.WorkspaceResponse;
import com.axlero.dashboardbuilder.entities.User;
import com.axlero.dashboardbuilder.entities.Workspace;
import com.axlero.dashboardbuilder.entities.WorkspaceMember;
import com.axlero.dashboardbuilder.enums.WorkspaceRole;
import com.axlero.dashboardbuilder.exceptions.BadRequestException;
import com.axlero.dashboardbuilder.exceptions.DuplicateResourceException;
import com.axlero.dashboardbuilder.exceptions.ResourceNotFoundException;
import com.axlero.dashboardbuilder.repositories.UserRepository;
import com.axlero.dashboardbuilder.repositories.WorkspaceMemberRepository;
import com.axlero.dashboardbuilder.repositories.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final WorkspaceAccessService access;

    @Transactional
    public WorkspaceResponse create(String email, WorkspaceRequest request) {
        User user = findUser(email);
        Workspace workspace = new Workspace();
        workspace.setName(request.name().trim());
        workspaceRepository.save(workspace);
        WorkspaceMember owner = memberRepository.save(new WorkspaceMember(workspace, user, WorkspaceRole.OWNER));
        log.info("Workspace created: id={}", workspace.getId());
        return toResponse(owner);
    }

    @Transactional(readOnly = true)
    public List<WorkspaceResponse> listMine(String email) {
        return memberRepository.findByUserEmailOrderByWorkspaceNameAsc(email).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public WorkspaceResponse get(String email, UUID workspaceId) {
        return toResponse(access.requireRole(workspaceId, email, WorkspaceRole.VIEWER));
    }

    @Transactional
    public WorkspaceResponse rename(String email, UUID workspaceId, WorkspaceRequest request) {
        WorkspaceMember member = access.requireRole(workspaceId, email, WorkspaceRole.OWNER);
        member.getWorkspace().setName(request.name().trim());
        log.info("Workspace renamed: id={}", workspaceId);
        return toResponse(member);
    }

    @Transactional
    public void delete(String email, UUID workspaceId) {
        WorkspaceMember member = access.requireRole(workspaceId, email, WorkspaceRole.OWNER);
        workspaceRepository.delete(member.getWorkspace());
        log.info("Workspace deleted: id={}", workspaceId);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> listMembers(String email, UUID workspaceId) {
        access.requireRole(workspaceId, email, WorkspaceRole.VIEWER);
        return memberRepository.findByWorkspaceIdOrderByCreatedAtAsc(workspaceId).stream()
                .map(m -> new MemberResponse(m.getUser().getId(), m.getUser().getEmail(),
                        m.getUser().getFullName(), m.getRole()))
                .toList();
    }

    @Transactional
    public MemberResponse addMember(String email, UUID workspaceId, AddMemberRequest request) {
        WorkspaceMember caller = access.requireRole(workspaceId, email, WorkspaceRole.OWNER);
        if (request.role() == WorkspaceRole.OWNER) {
            throw new BadRequestException("A workspace has a single OWNER; assign EDITOR or VIEWER");
        }
        User invitee = findUser(request.email().trim().toLowerCase(Locale.ROOT));
        if (memberRepository.existsByWorkspaceIdAndUserId(workspaceId, invitee.getId())) {
            throw new DuplicateResourceException("User is already a member of this workspace");
        }
        memberRepository.save(new WorkspaceMember(caller.getWorkspace(), invitee, request.role()));
        log.info("Member added: workspace={} user={} role={}", workspaceId, invitee.getId(), request.role());
        return new MemberResponse(invitee.getId(), invitee.getEmail(), invitee.getFullName(), request.role());
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }

    private WorkspaceResponse toResponse(WorkspaceMember member) {
        Workspace w = member.getWorkspace();
        return new WorkspaceResponse(w.getId(), w.getName(), member.getRole(), w.getCreatedAt());
    }
}