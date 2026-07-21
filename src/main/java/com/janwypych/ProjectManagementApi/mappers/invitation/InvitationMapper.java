package com.janwypych.ProjectManagementApi.mappers.invitation;

import com.janwypych.ProjectManagementApi.dtos.invitation.InvitationIdResponse;
import com.janwypych.ProjectManagementApi.dtos.invitation.ReceivedInvitationSummaryResponse;
import com.janwypych.ProjectManagementApi.dtos.invitation.SentInvitationDetailsResponse;
import com.janwypych.ProjectManagementApi.dtos.invitation.SentInvitationSummaryResponse;
import com.janwypych.ProjectManagementApi.entities.invitation.Invitation;
import com.janwypych.ProjectManagementApi.entities.user.User;
import org.springframework.stereotype.Component;

@Component
public class InvitationMapper {

    public InvitationIdResponse toIdResponse(Invitation savedInvitation) {
        return new InvitationIdResponse(savedInvitation.getId());
    }

        public SentInvitationSummaryResponse toSentSummaryResponse(Invitation invitation) {
            return SentInvitationSummaryResponse.builder()
                    .id(invitation.getId())
                    .senderUsername(invitation.getSenderWorkspaceMember().getUser().getUsername())
                    .senderRole(invitation.getSenderWorkspaceMember().getRole())
                    .receiverUsername(invitation.getReceiverUser().getUsername())
                    .status(invitation.getStatus())
                    .expiresAt(invitation.getExpiresAt())
                    .build();
        }

    public SentInvitationDetailsResponse toSentDetailsResponse(Invitation invitation) {
        return SentInvitationDetailsResponse.builder()
                .id(invitation.getId())
                .senderUsername(invitation.getSenderWorkspaceMember().getUser().getUsername())
                .senderEmail(invitation.getSenderWorkspaceMember().getUser().getEmail())
                .senderRole(invitation.getSenderWorkspaceMember().getRole())
                .receiverUsername(invitation.getReceiverUser().getUsername())
                .receiverEmail(invitation.getReceiverUser().getEmail())
                .status(invitation.getStatus())
                .createdAt(invitation.getCreatedAt())
                .expiresAt(invitation.getExpiresAt())
                .build();
    }

    public ReceivedInvitationSummaryResponse toReceivedInvitationSummaryResponse(Invitation invitation) {
        return ReceivedInvitationSummaryResponse.builder()
                .id(invitation.getId())
                .status(invitation.getStatus())
                .workspaceName(invitation.getWorkspace().getName())
                .senderUsername(invitation.getSenderWorkspaceMember().getUser().getUsername())
                .senderRole(invitation.getSenderWorkspaceMember().getRole())
                .expiresAt(invitation.getExpiresAt())
                .build();
    }
}
