package com.janwypych.ProjectManagementApi.controllers.invitation.sentInvitation;

import com.janwypych.ProjectManagementApi.BaseTest.invitation.sentInvitation.BaseTestSentInvitation;
import com.janwypych.ProjectManagementApi.dtos.invitation.sentInvitation.SentInvitationDetailsResponse;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.invitation.InvitationNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.services.invitation.InvitationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetSentInvitationTests extends BaseTestSentInvitation {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InvitationService invitationService;

    private Authentication createAuthentication() {
        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                null
        );
    }

    private RequestPostProcessor authenticatedUser() {
        return authentication(createAuthentication());
    }

    private void performGet(Long workspaceId, Long invitationId, ResultMatcher... matchers) throws Exception {
        var result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/workspaces/{workspaceId}/sentInvitations/{invitationId}", workspaceId, invitationId)
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    public void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/workspaces/1/sentInvitations/1"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceIsNotFound() throws Exception {
        when(invitationService.getSentInvitation(any(User.class), eq(workspace.getId()), eq(invitation.getId())))
                .thenThrow(new WorkspaceNotFoundException());

        performGet(workspace.getId(), invitation.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_NOT_FOUND"),
                jsonPath("$.message").value("Workspace not found"));
    }

    @Test
    public void shouldReturnHttp404WhenInvitationIsNotFound() throws Exception {
        when(invitationService.getSentInvitation(any(User.class), eq(workspace.getId()), eq(invitation.getId())))
                .thenThrow(new InvitationNotFoundException());

        performGet(workspace.getId(), invitation.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("INVITATION_NOT_FOUND"),
                jsonPath("$.message").value("Invitation not found"));
    }

    @Test
    public void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        SentInvitationDetailsResponse sentInvitationDetailsResponse = SentInvitationDetailsResponse.builder()
                .id(invitation.getId())
                .senderUsername(user.getUsername())
                .senderEmail(user.getEmail())
                .senderRole(workspaceMember.getRole())
                .receiverUsername(user2.getUsername())
                .receiverEmail(user2.getEmail())
                .status(invitation.getStatus())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        when(invitationService.getSentInvitation(any(User.class), eq(workspace.getId()), eq(invitation.getId())))
                .thenReturn(sentInvitationDetailsResponse);

        performGet(workspace.getId(), invitation.getId(),
                status().isOk(),
                jsonPath("$.id").value(invitation.getId()),
                jsonPath("$.senderUsername").value(user.getUsername()),
                jsonPath("$.senderEmail").value(user.getEmail()),
                jsonPath("$.senderRole").value(workspaceMember.getRole().toString()),
                jsonPath("$.receiverUsername").value(user2.getUsername()),
                jsonPath("$.receiverEmail").value(user2.getEmail()),
                jsonPath("$.status").value(invitation.getStatus().toString()),
                jsonPath("$.createdAt").isNotEmpty(),
                jsonPath("$.expiresAt").isNotEmpty());
    }
}
