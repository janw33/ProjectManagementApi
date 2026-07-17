package com.janwypych.ProjectManagementApi.controllers.invitation;

import com.janwypych.ProjectManagementApi.BaseTest.invitation.BaseTestInvitation;
import com.janwypych.ProjectManagementApi.dtos.invitation.CreateInvitationRequest;
import com.janwypych.ProjectManagementApi.dtos.invitation.InvitationIdResponse;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.invitation.PendingInvitationAlreadyExistsException;
import com.janwypych.ProjectManagementApi.exceptions.user.UserNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspaceMember.UserAlreadyWorkspaceMemberException;
import com.janwypych.ProjectManagementApi.services.invitation.InvitationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CreateInvitationTests extends BaseTestInvitation {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    private void performCreate(Long workspaceId, CreateInvitationRequest request, ResultMatcher... matchers) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        var result = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/workspaces/{workspaceId}/invitations", workspaceId)
                        .with(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    public void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        String requestJson = objectMapper.writeValueAsString(createInvitationRequest);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/workspaces/1/invitations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp400WhenReceiverUserIdIsNull() throws Exception {
        createInvitationRequest.setReceiverUserId(null);
        performCreate(workspace.getId(), createInvitationRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.receiverUserId").value("Receiver user id cannot be null"));
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceIsNotFound() throws Exception {
        when(invitationService.createInvitation(any(User.class), eq(createInvitationRequest), eq(workspace.getId())))
                .thenThrow(new WorkspaceNotFoundException());

        performCreate(workspace.getId(), createInvitationRequest,
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_NOT_FOUND"),
                jsonPath("$.message").value("Workspace not found"));
    }

    @Test
    public void shouldReturnHttp404WhenUserIsNotFound() throws Exception {
        when(invitationService.createInvitation(any(User.class), eq(createInvitationRequest), eq(workspace.getId())))
                .thenThrow(new UserNotFoundException());

        performCreate(workspace.getId(), createInvitationRequest,
                status().isNotFound(),
                jsonPath("$.error").value("USER_NOT_FOUND"),
                jsonPath("$.message").value("User not found"));
    }

    @Test
    public void shouldReturnHttp409WhenUserAlreadyWorkspaceMember() throws Exception {
        when(invitationService.createInvitation(any(User.class), eq(createInvitationRequest), eq(workspace.getId())))
                .thenThrow(new UserAlreadyWorkspaceMemberException());

        performCreate(workspace.getId(), createInvitationRequest,
                status().isConflict(),
                jsonPath("$.error").value("USER_ALREADY_WORKSPACE_MEMBER"),
                jsonPath("$.message").value("User is already in workspace"));
    }

    @Test
    public void shouldReturnHttp409WhenPendingInvitationAlreadyExists() throws Exception {
        when(invitationService.createInvitation(any(User.class), eq(createInvitationRequest), eq(workspace.getId())))
                .thenThrow(new PendingInvitationAlreadyExistsException());

        performCreate(workspace.getId(), createInvitationRequest,
                status().isConflict(),
                jsonPath("$.error").value("PENDING_INVITATION_ALREADY_EXISTS"),
                jsonPath("$.message").value("Pending invitation already exists"));
    }

    @Test
    public void shouldReturnHttp201WhenRequestIsValid() throws Exception {
        when(invitationService.createInvitation(any(User.class), eq(createInvitationRequest), eq(workspace.getId())))
                .thenReturn(new InvitationIdResponse(1L));

        performCreate(workspace.getId(), createInvitationRequest,
                status().isCreated(),
                jsonPath("$.id").value(1L));
    }
}
