package com.janwypych.ProjectManagementApi.controllers.invitation.receivedInvitation;

import com.janwypych.ProjectManagementApi.BaseTest.invitation.BaseTestReceivedInvitation;
import com.janwypych.ProjectManagementApi.BaseTest.user.BaseTestUser;
import com.janwypych.ProjectManagementApi.dtos.invitation.receivedInvitation.ReceivedInvitationDetailsResponse;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.invitation.InvitationNotFoundException;
import com.janwypych.ProjectManagementApi.services.invitation.InvitationService;
import com.janwypych.ProjectManagementApi.services.user.UserService;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetReceivedInvitationTests extends BaseTestReceivedInvitation {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InvitationService invitationService;

    private Authentication createAuthentication() {
        return new UsernamePasswordAuthenticationToken(
                receiverUser,
                null,
                null
        );
    }

    private RequestPostProcessor authenticatedUser() {
        return authentication(createAuthentication());
    }

    private void performGet(Long invitationId, ResultMatcher... matchers) throws Exception {
        var result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/receivedInvitations/{invitationId}", invitationId)
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    public void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/receivedInvitations/1"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp404WhenInvitationIsNotFound() throws Exception {
       when(invitationService.getReceivedInvitation(any(User.class), eq(invitation.getId())))
               .thenThrow(new InvitationNotFoundException());

       performGet(invitation.getId(),
               status().isNotFound(),
               jsonPath("$.error").value("INVITATION_NOT_FOUND"),
               jsonPath("$.message").value("Invitation not found"));
    }

    @Test
    public void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        ReceivedInvitationDetailsResponse response = ReceivedInvitationDetailsResponse.builder()
                .id(invitation.getId())
                .status(invitation.getStatus())
                .workspaceName(workspace.getName())
                .workspaceDescription(workspace.getDescription())
                .senderUsername(senderUser.getUsername())
                .senderEmail(senderUser.getEmail())
                .senderRole(senderWorkspaceMember.getRole())
                .createdAt(invitation.getCreatedAt())
                .expiresAt(invitation.getExpiresAt())
                .build();

        when(invitationService.getReceivedInvitation(any(User.class), eq(invitation.getId())))
                .thenReturn(response);

        performGet(invitation.getId(),
                status().isOk(),
                jsonPath("$.id").value(invitation.getId()),
                jsonPath("$.status").value(invitation.getStatus().toString()),
                jsonPath("$.workspaceName").value(workspace.getName()),
                jsonPath("$.workspaceDescription").value(workspace.getDescription()),
                jsonPath("$.senderUsername").value(senderUser.getUsername()),
                jsonPath("$.senderEmail").value(senderUser.getEmail()),
                jsonPath("$.senderRole").value(senderWorkspaceMember.getRole().toString()),
                jsonPath("$.createdAt").isNotEmpty(),
                jsonPath("$.expiresAt").isNotEmpty());
    }
}
