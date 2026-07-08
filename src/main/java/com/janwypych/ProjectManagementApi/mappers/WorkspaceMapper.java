package com.janwypych.ProjectManagementApi.mappers;

import com.janwypych.ProjectManagementApi.dtos.workspace.CreateWorkspaceRequest;
import com.janwypych.ProjectManagementApi.dtos.workspace.CreateWorkspaceResponse;
import com.janwypych.ProjectManagementApi.dtos.workspace.WorkspaceResponse;
import com.janwypych.ProjectManagementApi.entities.Workspace;
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
    public WorkspaceResponse toResponse(Workspace workspace) {
        return WorkspaceResponse.builder()
                .id(workspace.getId())
                .name(workspace.getName())
                .description(workspace.getDescription())
                .createdAt(workspace.getCreatedAt())
                .build();
    }
}
