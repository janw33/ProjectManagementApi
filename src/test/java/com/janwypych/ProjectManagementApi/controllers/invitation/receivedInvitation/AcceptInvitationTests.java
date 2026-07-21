package com.janwypych.ProjectManagementApi.controllers.invitation.receivedInvitation;

import com.janwypych.ProjectManagementApi.BaseTest.invitation.receivedInvitation.BaseTestReceivedInvitation;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.invitation.InvitationAlreadyProcessedException;
import com.janwypych.ProjectManagementApi.exceptions.invitation.InvitationExpiredException;
import com.janwypych.ProjectManagementApi.exceptions.invitation.InvitationNotFoundException;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AcceptInvitationTests extends BaseTestReceivedInvitation {
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

    private void performPost(Long invitationId, ResultMatcher... matchers) throws Exception {
        var result = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/receivedInvitations/{invitationId}/accept", invitationId)
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    public void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/receivedInvitations/1/accept"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp404WhenInvitationIsNotFound() throws Exception {
        doThrow(new InvitationNotFoundException())
                .when(invitationService)
                .acceptInvitation(
                        any(User.class),
                        eq(invitation.getId()));

        performPost(invitation.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("INVITATION_NOT_FOUND"),
                jsonPath("$.message").value("Invitation not found"));
    }

    @Test
    public void shouldReturnHttp409WhenInvitationExpired() throws Exception {
        doThrow(new InvitationExpiredException())
                .when(invitationService)
                .acceptInvitation(
                        any(User.class),
                        eq(invitation.getId()));

        performPost(invitation.getId(),
                status().isConflict(),
                jsonPath("$.error").value("INVITATION_EXPIRED"),
                jsonPath("$.message").value("Invitation has expired"));
    }

    @Test
    public void shouldReturnHttp409WhenInvitationWasAlreadyProcessed() throws Exception {
        doThrow(new InvitationAlreadyProcessedException())
                .when(invitationService)
                .acceptInvitation(
                        any(User.class),
                        eq(invitation.getId()));

        performPost(invitation.getId(),
                status().isConflict(),
                jsonPath("$.error").value("INVITATION_ALREADY_PROCESSED"),
                jsonPath("$.message").value("Invitation has already been processed"));
    }

    @Test
    public void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        performPost(invitation.getId(),
                status().isOk());
    }
}
