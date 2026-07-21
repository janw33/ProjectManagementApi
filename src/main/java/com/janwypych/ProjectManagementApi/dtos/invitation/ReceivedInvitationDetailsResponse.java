package com.janwypych.ProjectManagementApi.dtos.invitation;

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
public class ReceivedInvitationDetailsResponse {
    private Long id;
    private InvitationStatus status;
    private String workspaceName;
    private String workspaceDescription;
    private String senderUsername;
    private String senderEmail;
    private WorkspaceRole senderRole;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
