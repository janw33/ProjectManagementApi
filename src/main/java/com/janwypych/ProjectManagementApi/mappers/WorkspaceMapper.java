package com.janwypych.ProjectManagementApi.mappers;

import com.janwypych.ProjectManagementApi.dtos.workspace.CreateWorkspaceRequest;
import com.janwypych.ProjectManagementApi.dtos.workspace.CreateWorkspaceResponse;
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
    public CreateWorkspaceResponse toResponse(Workspace workspace) {
        return CreateWorkspaceResponse.builder()
                .id(workspace.getId())
                .name(workspace.getName())
                .description(workspace.getDescription())
                .createdAt(workspace.getCreatedAt())
                .build();
    }
}
