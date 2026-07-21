package com.janwypych.ProjectManagementApi.controllers.user;

import com.janwypych.ProjectManagementApi.BaseTest.user.BaseTestUser;
import com.janwypych.ProjectManagementApi.dtos.invitation.ReceivedInvitationSummaryResponse;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.services.user.UserService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetReceivedInvitationsTests extends BaseTestUser {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

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

    private void performGet(ResultMatcher... matchers) throws Exception {
        var result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/account/receivedInvitations")
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    public void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/account/receivedInvitations"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        ReceivedInvitationSummaryResponse response =
                ReceivedInvitationSummaryResponse.builder()
                        .id(invitation.getId())
                        .status(invitation.getStatus())
                        .workspaceName(workspace.getName())
                        .senderUsername(senderWorkspaceMember.getUser().getUsername())
                        .senderRole(senderWorkspaceMember.getRole())
                        .expiresAt(invitation.getExpiresAt())
                        .build();

        when(userService.getReceivedInvitations(any(User.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response)));

        performGet(
                status().isOk(),
                jsonPath("$.totalElements").value(1),
                jsonPath("$.content[0].id").value(invitation.getId()),
                jsonPath("$.content[0].status").value(invitation.getStatus().toString()),
                jsonPath("$.content[0].workspaceName").value(workspace.getName()),
                jsonPath("$.content[0].senderUsername").value(senderWorkspaceMember.getUser().getUsername()),
                jsonPath("$.content[0].senderRole").value(senderWorkspaceMember.getRole().toString()),
                jsonPath("$.content[0].expiresAt").isNotEmpty());
    }
}
