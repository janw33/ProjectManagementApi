package com.janwypych.ProjectManagementApi.controllers.projectMember;

import com.janwypych.ProjectManagementApi.dtos.projectMember.CreateProjectMemberRequest;
import com.janwypych.ProjectManagementApi.dtos.projectMember.ProjectMemberResponse;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.services.projectMember.ProjectMemberService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/projects/{projectId}/members")
public class ProjectMemberController {
    private final ProjectMemberService projectMemberService;

    public ProjectMemberController(ProjectMemberService projectMemberService) {
        this.projectMemberService = projectMemberService;
    }

    @PostMapping
    public ResponseEntity<Void> createProjectMember(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid CreateProjectMemberRequest request,
            @PathVariable("workspaceId") Long workspaceId,
            @PathVariable("projectId") Long projectId
    ) {
        projectMemberService.createProjectMember(currentUser, request, workspaceId, projectId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<Page<ProjectMemberResponse>> getProjectMembers(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("workspaceId") Long workspaceId,
            @PathVariable("projectId") Long projectId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(projectMemberService.getProjectMembers(currentUser, workspaceId, projectId, pageable));
    }
}
