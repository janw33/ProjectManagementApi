package com.janwypych.ProjectManagementApi.controllers;

import com.janwypych.ProjectManagementApi.dtos.Project.*;
import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.services.ProjectService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @GetMapping
    public ResponseEntity<Page<ProjectSummaryResponse>> getProjects(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("workspaceId") Long workspaceId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(projectService.getProjects(currentUser, workspaceId, pageable));
    }

    @GetMapping(path = "/{projectId}")
    public ResponseEntity<ProjectDetailsResponse> getProject(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("workspaceId") Long workspaceId,
            @PathVariable("projectId") Long projectId
    ) {
        return ResponseEntity.ok(projectService.getProject(currentUser, workspaceId, projectId));
    }

    @PatchMapping(path = "/{projectId}")
    public ResponseEntity<ProjectIdResponse> updateProject(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid UpdateProjectRequest request,
            @PathVariable("workspaceId") Long workspaceId,
            @PathVariable("projectId") Long projectId
    ) {
        return ResponseEntity.ok(projectService.updateProject(currentUser, request, workspaceId, projectId));
    }
}
