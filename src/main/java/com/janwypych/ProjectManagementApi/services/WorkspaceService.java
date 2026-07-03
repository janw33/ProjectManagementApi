package com.janwypych.ProjectManagementApi.services;

import com.janwypych.ProjectManagementApi.dtos.workspace.CreateWorkspaceRequest;
import com.janwypych.ProjectManagementApi.dtos.workspace.WorkspaceResponse;
import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.entities.Workspace;
import com.janwypych.ProjectManagementApi.entities.WorkspaceMember;
import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;
import com.janwypych.ProjectManagementApi.mappers.WorkspaceMapper;
import com.janwypych.ProjectManagementApi.repositories.WorkspaceMemberRepository;
import com.janwypych.ProjectManagementApi.repositories.WorkspaceRepository;
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
    public WorkspaceResponse createWorkspace(User currentUser, CreateWorkspaceRequest createWorkspaceRequest) {
        Workspace workspace = workspaceMapper.toEntity(createWorkspaceRequest);

        Workspace savedWorkspace = workspaceRepository.save(workspace);

        WorkspaceMember workspaceMember = WorkspaceMember.builder()
                .role(WorkspaceRole.OWNER)
                .user(currentUser)
                .workspace(savedWorkspace)
                .build();

        workspaceMemberRepository.save(workspaceMember);

        return workspaceMapper.toResponse(savedWorkspace);
    }

    public Page<WorkspaceResponse> getWorkspaces(User currentUser, Pageable pageable) {
        Page<WorkspaceMember> page = workspaceMemberRepository.findAllByUser(currentUser ,pageable);
        return page.map(workspaceMember -> workspaceMapper.toResponse(workspaceMember.getWorkspace()));
    }
}
