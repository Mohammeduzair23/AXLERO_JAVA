
package com.axlero.dashboardbuilder.workspace.service;

import com.axlero.dashboardbuilder.common.exception.ResourceNotFoundException;
import com.axlero.dashboardbuilder.user.entity.User;
import com.axlero.dashboardbuilder.user.repository.UserRepository;
import com.axlero.dashboardbuilder.workspace.dto.WorkspaceRequest;
import com.axlero.dashboardbuilder.workspace.dto.WorkspaceResponse;
import com.axlero.dashboardbuilder.workspace.entity.Workspace;
import com.axlero.dashboardbuilder.workspace.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;

    public WorkspaceService(WorkspaceRepository workspaceRepository,
                            UserRepository userRepository) {
        this.workspaceRepository = workspaceRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public WorkspaceResponse createWorkspace(WorkspaceRequest request, String email) {

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));

        Workspace workspace = new Workspace();
        workspace.setName(request.name().trim());
        workspace.setOwner(owner);

        Workspace savedWorkspace = workspaceRepository.save(workspace);

        return new WorkspaceResponse(
                savedWorkspace.getId(),
                savedWorkspace.getName(),
                savedWorkspace.getCreatedAt()
        );
    }
}
