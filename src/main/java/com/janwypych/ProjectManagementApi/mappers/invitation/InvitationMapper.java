package com.janwypych.ProjectManagementApi.mappers.invitation;

import com.janwypych.ProjectManagementApi.dtos.invitation.InvitationIdResponse;
import com.janwypych.ProjectManagementApi.entities.invitation.Invitation;
import org.springframework.stereotype.Component;

@Component
public class InvitationMapper {

    public InvitationIdResponse toIdResponse(Invitation savedInvitation) {
        return new InvitationIdResponse(savedInvitation.getId());
    }
}
