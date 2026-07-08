package com.janwypych.ProjectManagementApi.controllers;

import com.janwypych.ProjectManagementApi.dtos.workspace.CreateWorkspaceRequest;
import com.janwypych.ProjectManagementApi.dtos.workspace.CreateWorkspaceResponse;
import com.janwypych.ProjectManagementApi.dtos.workspace.WorkspaceResponse;
import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.services.WorkspaceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/workspaces")
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @PostMapping()
    public ResponseEntity<CreateWorkspaceResponse> createWorkspace(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid CreateWorkspaceRequest createWorkspaceRequest
            ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workspaceService.createWorkspace(currentUser, createWorkspaceRequest));
    }

    @GetMapping(path = "")
    public ResponseEntity<Page<WorkspaceResponse>> getWorkspaces(
            @AuthenticationPrincipal User currentUser,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                workspaceService.getWorkspaces(currentUser, pageable)
        );
    }
}
