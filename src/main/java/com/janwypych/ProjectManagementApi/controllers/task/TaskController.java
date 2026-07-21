package com.janwypych.ProjectManagementApi.controllers.task;

import com.janwypych.ProjectManagementApi.dtos.task.*;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.services.task.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @GetMapping
    public ResponseEntity<Page<TaskSummaryResponse>> getTasks(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("workspaceId") Long workspaceId,
            @PathVariable("projectId") Long projectId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(taskService.getTasks(currentUser, workspaceId, projectId, pageable));
    }

    @GetMapping(path = "/{taskId}")
    public ResponseEntity<TaskDetailsResponse> getTask(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("workspaceId") Long workspaceId,
            @PathVariable("projectId") Long projectId,
            @PathVariable("taskId") Long taskId
    ) {
        return ResponseEntity.ok(taskService.getTask(currentUser, workspaceId, projectId, taskId));
    }

    @PatchMapping(path = "/{taskId}")
    public ResponseEntity<TaskIdResponse> updateTask(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid UpdateTaskRequest updateTaskRequest,
            @PathVariable("workspaceId") Long workspaceId,
            @PathVariable("projectId") Long projectId,
            @PathVariable("taskId") Long taskId
    ) {
        return ResponseEntity.ok(taskService.updateTask(currentUser, updateTaskRequest, workspaceId, projectId, taskId));
    }

    @DeleteMapping(path = "/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("workspaceId") Long workspaceId,
            @PathVariable("projectId") Long projectId,
            @PathVariable("taskId") Long taskId
    ) {
        taskService.deleteTask(currentUser, workspaceId, projectId, taskId);
        return ResponseEntity.noContent().build();
    }
}
