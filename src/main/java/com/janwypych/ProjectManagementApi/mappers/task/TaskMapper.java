package com.janwypych.ProjectManagementApi.mappers.task;

import com.janwypych.ProjectManagementApi.dtos.task.CreateTaskRequest;
import com.janwypych.ProjectManagementApi.entities.task.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public Task toEntity(CreateTaskRequest request) {
        return Task.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }
}
