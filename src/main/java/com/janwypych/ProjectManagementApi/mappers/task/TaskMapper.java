package com.janwypych.ProjectManagementApi.mappers.task;

import com.janwypych.ProjectManagementApi.dtos.task.CreateTaskRequest;
import com.janwypych.ProjectManagementApi.dtos.task.TaskIdResponse;
import com.janwypych.ProjectManagementApi.entities.enums.TaskStatus;
import com.janwypych.ProjectManagementApi.entities.project.Project;
import com.janwypych.ProjectManagementApi.entities.projectMember.ProjectMember;
import com.janwypych.ProjectManagementApi.entities.task.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public Task toEntity(CreateTaskRequest request, Project project, ProjectMember projectMember) {
        return Task.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status(TaskStatus.NOT_STARTED)
                .project(project)
                .assignee(projectMember)
                .build();
    }

    public TaskIdResponse toIdResponse(Task savedTask) {
        return new TaskIdResponse(savedTask.getId());
    }
}
