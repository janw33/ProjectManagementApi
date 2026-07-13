package com.janwypych.ProjectManagementApi.mappers.project;

import com.janwypych.ProjectManagementApi.dtos.project.CreateProjectRequest;
import com.janwypych.ProjectManagementApi.dtos.project.ProjectDetailsResponse;
import com.janwypych.ProjectManagementApi.dtos.project.ProjectIdResponse;
import com.janwypych.ProjectManagementApi.dtos.project.ProjectSummaryResponse;
import com.janwypych.ProjectManagementApi.entities.project.Project;
import com.janwypych.ProjectManagementApi.entities.workspace.Workspace;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {
    public Project toEntity(CreateProjectRequest createProjectRequest, Workspace workspace) {
        return Project.builder()
                .name(createProjectRequest.getName())
                .description(createProjectRequest.getDescription())
                .workspace(workspace)
                .build();
    }

    public ProjectIdResponse toIdResponse(Project project) {
        return new ProjectIdResponse(project.getId());
    }

    public ProjectSummaryResponse toSummaryResponse(Project project, boolean hasAccess) {
        return ProjectSummaryResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .hasAccess(hasAccess)
                .build();
    }

    public ProjectDetailsResponse toDetailsResponse(Project project) {
        return ProjectDetailsResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}
