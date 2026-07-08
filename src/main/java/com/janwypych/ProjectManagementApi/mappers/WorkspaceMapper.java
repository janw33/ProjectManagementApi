package com.janwypych.ProjectManagementApi.mappers;

import com.janwypych.ProjectManagementApi.dtos.workspace.CreateWorkspaceRequest;
import com.janwypych.ProjectManagementApi.dtos.workspace.CreateWorkspaceResponse;
import com.janwypych.ProjectManagementApi.dtos.workspace.WorkspaceDetailsResponse;
import com.janwypych.ProjectManagementApi.dtos.workspace.WorkspaceSummaryResponse;
import com.janwypych.ProjectManagementApi.entities.Workspace;
import com.janwypych.ProjectManagementApi.entities.WorkspaceMember;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceMapper {
    public Workspace toEntity(CreateWorkspaceRequest createWorkspaceRequest) {
        return Workspace.builder()
                .name(createWorkspaceRequest.getName())
                .description(createWorkspaceRequest.getDescription())
                .build();
    }

    public CreateWorkspaceResponse toCreateResponse(Workspace workspace) {
        return new CreateWorkspaceResponse(workspace.getId());
    }

    public WorkspaceSummaryResponse toSummaryResponse(WorkspaceMember workspaceMember) {
        Workspace workspace = workspaceMember.getWorkspace();

        return WorkspaceSummaryResponse.builder()
                .id(workspace.getId())
                .name(workspace.getName())
                .role(workspaceMember.getRole())
                .build();
    }

    public WorkspaceDetailsResponse toDetailsResponse(WorkspaceMember workspaceMember) {
        Workspace workspace = workspaceMember.getWorkspace();

        return WorkspaceDetailsResponse.builder()
                .id(workspace.getId())
                .name(workspace.getName())
                .description(workspace.getDescription())
                .createdAt(workspace.getCreatedAt())
                .role(workspaceMember.getRole())
                .build();
    }
}
