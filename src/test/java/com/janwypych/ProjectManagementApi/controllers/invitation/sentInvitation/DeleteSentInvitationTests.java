package com.janwypych.ProjectManagementApi.controllers.invitation.sentInvitation;

import com.janwypych.ProjectManagementApi.BaseTest.invitation.sentInvitation.BaseTestSentInvitation;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.invitation.InvitationAlreadyProcessedException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DeleteSentInvitationTests extends BaseTestSentInvitation {
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

    private void performDelete(Long workspaceId, Long invitationId, ResultMatcher... matchers) throws Exception {
        var result = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/workspaces/{workspaceId}/sentInvitations/{invitationId}", workspaceId, invitationId)
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    public void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/workspaces/1/sentInvitations/1"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceIsNotFound() throws Exception {
        doThrow(new WorkspaceNotFoundException())
                .when(invitationService)
                .deleteSentInvitation(
                        any(User.class),
                        eq(workspace.getId()),
                        eq(invitation.getId()));

        performDelete(workspace.getId(), invitation.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_NOT_FOUND"),
                jsonPath("$.message").value("Workspace not found"));
    }

    @Test
    public void shouldReturnHttp404WhenInvitationIsNotFound() throws Exception {
        doThrow(new InvitationNotFoundException())
                .when(invitationService)
                .deleteSentInvitation(
                        any(User.class),
                        eq(workspace.getId()),
                        eq(invitation.getId()));

        performDelete(workspace.getId(), invitation.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("INVITATION_NOT_FOUND"),
                jsonPath("$.message").value("Invitation not found"));
    }

    @Test
    public void shouldReturnHttp409WhenInvitationIsAlreadyProcessed() throws Exception {
        doThrow(new InvitationAlreadyProcessedException())
                .when(invitationService)
                .deleteSentInvitation(
                        any(User.class),
                        eq(workspace.getId()),
                        eq(invitation.getId()));

        performDelete(workspace.getId(), invitation.getId(),
                status().isConflict(),
                jsonPath("$.error").value("INVITATION_ALREADY_PROCESSED"),
                jsonPath("$.message").value("Invitation has already been processed"));
    }

    @Test
    public void shouldReturnHttp204WhenRequestIsValid() throws Exception {
        performDelete(workspace.getId(), invitation.getId(),
                status().isNoContent());
    }
}
