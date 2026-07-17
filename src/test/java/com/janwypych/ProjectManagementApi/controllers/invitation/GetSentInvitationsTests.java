package com.janwypych.ProjectManagementApi.controllers.invitation;

import com.janwypych.ProjectManagementApi.BaseTest.invitation.BaseTestInvitation;
import com.janwypych.ProjectManagementApi.dtos.invitation.SentInvitationSummaryResponse;
import com.janwypych.ProjectManagementApi.entities.enums.InvitationStatus;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.services.invitation.InvitationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetSentInvitationsTests extends BaseTestInvitation {
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

    private void performGet(Long workspaceId, ResultMatcher... matchers) throws Exception {
        var result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/workspaces/{workspaceId}/invitations", workspaceId)
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    public void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/workspaces/1/invitations"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceIsNotFound() throws Exception {
        when(invitationService.getSentInvitations(any(User.class), eq(workspace.getId()), any(Pageable.class)))
                .thenThrow(new WorkspaceNotFoundException());

        performGet(workspace.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_NOT_FOUND"),
                jsonPath("$.message").value("Workspace not found"));
    }

    @Test
    public void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        SentInvitationSummaryResponse sentInvitationSummaryResponse = SentInvitationSummaryResponse.builder()
                .id(invitation.getId())
                .senderUsername(user.getUsername())
                .senderRole(workspaceMember.getRole())
                .receiverUsername(user2.getUsername())
                .status(InvitationStatus.DENIED)
                .expiresAt(LocalDateTime.now())
                .build();

        when(invitationService.getSentInvitations(any(User.class), eq(workspace.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sentInvitationSummaryResponse)));

        performGet(workspace.getId(),
                status().isOk(),
                jsonPath("$.content").isArray(),
                jsonPath("$.totalElements").value(1),
                jsonPath("$.content[0].id").value(invitation.getId()),
                jsonPath("$.content[0].senderUsername").value(user.getUsername()),
                jsonPath("$.content[0].senderRole").value(workspaceMember.getRole().toString()),
                jsonPath("$.content[0].receiverUsername").value(user2.getUsername()),
                jsonPath("$.content[0].status").value(InvitationStatus.DENIED.toString()),
                jsonPath("$.content[0].expiresAt").isNotEmpty());

    }
}
