package com.janwypych.ProjectManagementApi.mappers.invitation;

import com.janwypych.ProjectManagementApi.dtos.invitation.InvitationIdResponse;
import com.janwypych.ProjectManagementApi.dtos.invitation.SentInvitationSummaryResponse;
import com.janwypych.ProjectManagementApi.entities.invitation.Invitation;
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
}
