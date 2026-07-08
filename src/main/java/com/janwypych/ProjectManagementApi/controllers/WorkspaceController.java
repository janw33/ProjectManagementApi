package com.janwypych.ProjectManagementApi.controllers;

import com.janwypych.ProjectManagementApi.dtos.workspace.*;
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
    public ResponseEntity<WorkspaceIdResponse> createWorkspace(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid CreateWorkspaceRequest createWorkspaceRequest
            ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workspaceService.createWorkspace(currentUser, createWorkspaceRequest));
    }

    @GetMapping()
    public ResponseEntity<Page<WorkspaceSummaryResponse>> getWorkspaces(
            @AuthenticationPrincipal User currentUser,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                workspaceService.getWorkspaces(currentUser, pageable)
        );
    }

    @GetMapping(path = "/{workspaceId}")
    public ResponseEntity<WorkspaceDetailsResponse> getWorkspace(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("workspaceId") Long workspaceId
    ) {
        return ResponseEntity.ok(workspaceService.getWorkspace(currentUser, workspaceId));
    }

    @PatchMapping(path = "/{workspaceId}")
    public ResponseEntity<WorkspaceIdResponse> updateWorkspace (
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid UpdateWorkspaceRequest updateWorkspaceRequest,
            @PathVariable("workspaceId") Long workspaceId
    ) {
        return ResponseEntity.ok(workspaceService.updateWorkspace(currentUser, updateWorkspaceRequest, workspaceId));
    }
}
