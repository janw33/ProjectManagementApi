package com.janwypych.ProjectManagementApi.controllers.invitation;

import com.janwypych.ProjectManagementApi.dtos.invitation.CreateInvitationRequest;
import com.janwypych.ProjectManagementApi.dtos.invitation.InvitationIdResponse;
import com.janwypych.ProjectManagementApi.dtos.invitation.SentInvitationDetailsResponse;
import com.janwypych.ProjectManagementApi.dtos.invitation.SentInvitationSummaryResponse;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.services.invitation.InvitationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/invitations")
public class InvitationController {
    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @PostMapping
    public ResponseEntity<InvitationIdResponse> createInvitation(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid CreateInvitationRequest request,
            @PathVariable("workspaceId") Long workspaceId
            ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invitationService.createInvitation(currentUser, request, workspaceId));
    }

    @GetMapping
    public ResponseEntity<Page<SentInvitationSummaryResponse>> getSentInvitations(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("workspaceId") Long workspaceId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(invitationService.getSentInvitations(currentUser, workspaceId, pageable));
    }

    @GetMapping("/{invitationId}")
    public ResponseEntity<SentInvitationDetailsResponse> getSentInvitation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("workspaceId") Long workspaceId,
            @PathVariable("invitationId") Long invitationId
    ) {
        return ResponseEntity.ok(invitationService.getSentInvitation(currentUser, workspaceId, invitationId));
    }
}
