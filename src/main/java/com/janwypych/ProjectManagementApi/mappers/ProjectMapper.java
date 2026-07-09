package com.janwypych.ProjectManagementApi.mappers;

import com.janwypych.ProjectManagementApi.dtos.Project.CreateProjectRequest;
import com.janwypych.ProjectManagementApi.dtos.Project.ProjectDetailsResponse;
import com.janwypych.ProjectManagementApi.dtos.Project.ProjectIdResponse;
import com.janwypych.ProjectManagementApi.dtos.Project.ProjectSummaryResponse;
import com.janwypych.ProjectManagementApi.entities.Project;
import com.janwypych.ProjectManagementApi.entities.Workspace;
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
