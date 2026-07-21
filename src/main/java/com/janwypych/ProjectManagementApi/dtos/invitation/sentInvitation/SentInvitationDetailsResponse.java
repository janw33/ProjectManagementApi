package com.janwypych.ProjectManagementApi.dtos.invitation.sentInvitation;

import com.janwypych.ProjectManagementApi.entities.enums.InvitationStatus;
import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SentInvitationDetailsResponse {
    private Long id;
    private String senderUsername;
    private String senderEmail;
    private WorkspaceRole senderRole;
    private String receiverUsername;
    private String receiverEmail;
    private InvitationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
