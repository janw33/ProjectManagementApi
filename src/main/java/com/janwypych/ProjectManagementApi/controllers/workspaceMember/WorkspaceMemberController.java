package com.janwypych.ProjectManagementApi.controllers.workspaceMember;

import com.janwypych.ProjectManagementApi.dtos.workspaceMember.UpdateWorkspaceMemberRequest;
import com.janwypych.ProjectManagementApi.dtos.workspaceMember.WorkspaceMemberDetailsResponse;
import com.janwypych.ProjectManagementApi.dtos.workspaceMember.WorkspaceMemberSummaryResponse;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.services.workspaceMember.WorkspaceMemberService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/members")
public class WorkspaceMemberController {
    private final WorkspaceMemberService workspaceMemberService;

    public WorkspaceMemberController(WorkspaceMemberService workspaceMemberService) {
        this.workspaceMemberService = workspaceMemberService;
    }

    @GetMapping
    public ResponseEntity<Page<WorkspaceMemberSummaryResponse>> getWorkspaceMembers(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("workspaceId") Long workspaceId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(workspaceMemberService.getWorkspaceMembers(currentUser, workspaceId, pageable));
    }

    @GetMapping(path = "/{memberId}")
    public ResponseEntity<WorkspaceMemberDetailsResponse> getWorkspaceMember(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("workspaceId") Long workspaceId,
            @PathVariable("memberId") Long memberId
    ) {
        return ResponseEntity.ok(workspaceMemberService.getWorkspaceMember(currentUser, workspaceId, memberId));
    }

    @PatchMapping(path = "/{memberId}")
    public ResponseEntity<Void> updateWorkspaceMember(
            @AuthenticationPrincipal User currentUser,
            @RequestBody UpdateWorkspaceMemberRequest request,
            @PathVariable("workspaceId") Long workspaceId,
            @PathVariable("memberId") Long memberId
    ) {
        workspaceMemberService.updateWorkspaceMember(currentUser, request, workspaceId, memberId);
        return ResponseEntity.ok().build();
    }
}
