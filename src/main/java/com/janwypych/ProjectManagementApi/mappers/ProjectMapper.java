package com.janwypych.ProjectManagementApi.mappers;

import com.janwypych.ProjectManagementApi.dtos.CreateProjectRequest;
import com.janwypych.ProjectManagementApi.dtos.ProjectIdResponse;
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
}
