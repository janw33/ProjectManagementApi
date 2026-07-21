package com.janwypych.ProjectManagementApi.controllers.invitation.receivedInvitation;

import com.janwypych.ProjectManagementApi.dtos.invitation.receivedInvitation.ReceivedInvitationDetailsResponse;
import com.janwypych.ProjectManagementApi.dtos.invitation.receivedInvitation.ReceivedInvitationSummaryResponse;
import com.janwypych.ProjectManagementApi.entities.enums.InvitationStatus;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.services.invitation.InvitationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/receivedInvitations")
public class ReceivedInvitationController {
    private final InvitationService invitationService;

    public ReceivedInvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @GetMapping()
    public ResponseEntity<Page<ReceivedInvitationSummaryResponse>> getReceivedInvitations(
            @AuthenticationPrincipal User currentUser,
            Pageable pageable
    ) {
        return ResponseEntity.ok(invitationService.getReceivedInvitations(currentUser, pageable));
    }

    @GetMapping("/{invitationId}")
    public ResponseEntity<ReceivedInvitationDetailsResponse> getReceivedInvitation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("invitationId") Long invitationId
    ) {
        return ResponseEntity.ok(invitationService.getReceivedInvitation(currentUser, invitationId));
    }

    @PostMapping("/{invitationId}/accept")
    public ResponseEntity<Void> acceptInvitation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("invitationId") Long invitationId
    ) {
        invitationService.acceptInvitation(currentUser, invitationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{invitationId}/decline")
    public ResponseEntity<Void> declineInvitation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("invitationId") Long invitationId
    ) {
        invitationService.declineInvitation(currentUser, invitationId);
        return ResponseEntity.ok().build();
    }
}
