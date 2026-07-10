package com.janwypych.ProjectManagementApi.controllers.task;

import com.janwypych.ProjectManagementApi.dtos.task.CreateTaskRequest;
import com.janwypych.ProjectManagementApi.dtos.task.TaskIdResponse;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.services.task.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/projects/{projectId}/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskIdResponse> createTask(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid CreateTaskRequest request,
            @PathVariable("workspaceId") Long workspaceId,
            @PathVariable("projectId") Long projectId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(currentUser, request, workspaceId, projectId));
    }
}
