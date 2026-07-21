package com.janwypych.ProjectManagementApi.services.workspace;

import com.janwypych.ProjectManagementApi.dtos.workspace.*;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import com.janwypych.ProjectManagementApi.entities.workspaceMember.WorkspaceMember;
import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.workspace.WorkspaceMapper;
import com.janwypych.ProjectManagementApi.repositories.workspaceMember.WorkspaceMemberRepository;
import com.janwypych.ProjectManagementApi.repositories.workspace.WorkspaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkspaceService {
    private final WorkspaceMapper workspaceMapper;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public WorkspaceService(WorkspaceMapper workspaceMapper, WorkspaceRepository workspaceRepository, WorkspaceMemberRepository workspaceMemberRepository) {
        this.workspaceMapper = workspaceMapper;
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Transactional
    public WorkspaceIdResponse createWorkspace(User currentUser, CreateWorkspaceRequest createWorkspaceRequest) {
        Workspace workspace = workspaceMapper.toEntity(createWorkspaceRequest);

        Workspace savedWorkspace = workspaceRepository.save(workspace);

        WorkspaceMember workspaceMember = WorkspaceMember.builder()
                .role(WorkspaceRole.OWNER)
                .user(currentUser)
                .workspace(savedWorkspace)
                .build();

        workspaceMemberRepository.save(workspaceMember);

        return workspaceMapper.toIdResponse(savedWorkspace);
    }

    public Page<WorkspaceSummaryResponse> getWorkspaces(User currentUser, Pageable pageable) {
        return workspaceMemberRepository.findAllByUserOrderByWorkspaceUpdatedAtDesc(currentUser, pageable)
                .map(workspaceMapper::toSummaryResponse);
    }

    public WorkspaceDetailsResponse getWorkspace(User currentUser, Long id) {
        WorkspaceMember member = workspaceMemberRepository.findByWorkspaceIdAndUser(id, currentUser)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        return workspaceMapper.toDetailsResponse(member);
    }

    @Transactional
    public WorkspaceIdResponse updateWorkspace(User currentUser, UpdateWorkspaceRequest updateWorkspaceRequest, Long workspaceId) {
        WorkspaceMember member = workspaceMemberRepository.findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        Workspace workspace = member.getWorkspace();

        if (updateWorkspaceRequest.getName() != null) {
            workspace.setName(updateWorkspaceRequest.getName());
        }

        if (updateWorkspaceRequest.getDescription() != null) {
            workspace.setDescription(updateWorkspaceRequest.getDescription());
        }

        return workspaceMapper.toIdResponse(workspace);
    }

    public void deleteWorkspace(User currentUser, Long workspaceId) {
        WorkspaceMember member = workspaceMemberRepository.findByWorkspaceIdAndUser(workspaceId, currentUser)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        Workspace workspace = member.getWorkspace();

        workspaceRepository.delete(workspace);
    }
}
