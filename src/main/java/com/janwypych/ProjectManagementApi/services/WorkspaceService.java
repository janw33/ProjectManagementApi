package com.janwypych.ProjectManagementApi.services;

import com.janwypych.ProjectManagementApi.dtos.workspace.CreateWorkspaceRequest;
import com.janwypych.ProjectManagementApi.dtos.workspace.CreateWorkspaceResponse;
import com.janwypych.ProjectManagementApi.dtos.workspace.WorkspaceDetailsResponse;
import com.janwypych.ProjectManagementApi.dtos.workspace.WorkspaceSummaryResponse;
import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.entities.Workspace;
import com.janwypych.ProjectManagementApi.entities.WorkspaceMember;
import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;
import com.janwypych.ProjectManagementApi.exceptions.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.mappers.WorkspaceMapper;
import com.janwypych.ProjectManagementApi.repositories.WorkspaceMemberRepository;
import com.janwypych.ProjectManagementApi.repositories.WorkspaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public CreateWorkspaceResponse createWorkspace(User currentUser, CreateWorkspaceRequest createWorkspaceRequest) {
        Workspace workspace = workspaceMapper.toEntity(createWorkspaceRequest);

        Workspace savedWorkspace = workspaceRepository.save(workspace);

        WorkspaceMember workspaceMember = WorkspaceMember.builder()
                .role(WorkspaceRole.OWNER)
                .user(currentUser)
                .workspace(savedWorkspace)
                .build();

        workspaceMemberRepository.save(workspaceMember);

        return workspaceMapper.toCreateResponse(savedWorkspace);
    }

    public Page<WorkspaceSummaryResponse> getWorkspaces(User currentUser, Pageable pageable) {
        return workspaceMemberRepository.findAllByUser(currentUser, pageable)
                .map(workspaceMapper::toSummaryResponse);
    }

    public WorkspaceDetailsResponse getWorkspace(User currentUser, Long id) {
        WorkspaceMember member = workspaceMemberRepository.findByWorkspaceIdAndUser(id, currentUser)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        return workspaceMapper.toDetailsResponse(member);
    }
}
