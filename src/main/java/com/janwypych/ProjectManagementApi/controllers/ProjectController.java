package com.janwypych.ProjectManagementApi.controllers;

import com.janwypych.ProjectManagementApi.dtos.CreateProjectRequest;
import com.janwypych.ProjectManagementApi.dtos.ProjectIdResponse;
import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.services.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectIdResponse> createProject(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid CreateProjectRequest createProjectRequest,
            @PathVariable("workspaceId") Long workspaceId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(currentUser, createProjectRequest, workspaceId));
    }
}
