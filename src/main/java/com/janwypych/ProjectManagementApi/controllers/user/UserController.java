package com.janwypych.ProjectManagementApi.controllers.user;

import com.janwypych.ProjectManagementApi.dtos.invitation.ReceivedInvitationDetailsResponse;
import com.janwypych.ProjectManagementApi.dtos.invitation.ReceivedInvitationSummaryResponse;
import com.janwypych.ProjectManagementApi.dtos.user.UpdateCurrentUserRequest;
import com.janwypych.ProjectManagementApi.dtos.user.UserResponse;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.services.user.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserResponse> getCurrentUser(
            @AuthenticationPrincipal User currentUser
            ) {
        return ResponseEntity.ok(userService.getCurrentUser(currentUser));
    }

    @PatchMapping
    public ResponseEntity<Void> updateCurrentUser( // dodaj haslo sprawdzenie dotychczasowego hasla
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid UpdateCurrentUserRequest request
    ) {
        userService.updateCurrentUser(currentUser, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/receivedInvitations")
    public ResponseEntity<Page<ReceivedInvitationSummaryResponse>> getReceivedInvitations(
            @AuthenticationPrincipal User currentUser,
            Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getReceivedInvitations(currentUser, pageable));
    }

    @GetMapping("/receivedInvitations/{invitationId}")
    public ResponseEntity<ReceivedInvitationDetailsResponse> getReceivedInvitation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("invitationId") Long invitationId
    ) {
        return ResponseEntity.ok(userService.getReceivedInvitation(currentUser, invitationId));
    }

}
