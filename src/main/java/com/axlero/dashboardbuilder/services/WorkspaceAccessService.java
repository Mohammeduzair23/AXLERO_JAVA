package com.axlero.dashboardbuilder.services;

import com.axlero.dashboardbuilder.entities.WorkspaceMember;
import com.axlero.dashboardbuilder.enums.WorkspaceRole;
import com.axlero.dashboardbuilder.exceptions.ResourceNotFoundException;
import com.axlero.dashboardbuilder.repositories.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * The single place where tenant isolation and RBAC are decided.
 * Non-members get 404 (so workspace ids cannot be probed); members with too low a role get 403.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceAccessService {

    private final WorkspaceMemberRepository memberRepository;

    @Transactional(readOnly = true)
    public WorkspaceMember requireRole(UUID workspaceId, String email, WorkspaceRole minimum) {
        WorkspaceMember member = memberRepository.findByWorkspaceIdAndUserEmail(workspaceId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", workspaceId));
        if (!member.getRole().atLeast(minimum)) {
            log.warn("Access denied: workspace={} role={} required={}", workspaceId, member.getRole(), minimum);
            throw new AccessDeniedException("Requires workspace role " + minimum);
        }
        return member;
    }
}