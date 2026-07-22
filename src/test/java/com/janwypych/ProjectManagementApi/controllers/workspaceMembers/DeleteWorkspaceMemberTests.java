package com.janwypych.ProjectManagementApi.controllers.workspaceMembers;

import com.janwypych.ProjectManagementApi.BaseTest.workspaceMember.BaseTestWorkspaceMembers;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspaceMember.WorkspaceMemberAlreadyDeletedException;
import com.janwypych.ProjectManagementApi.exceptions.workspaceMember.WorkspaceMemberNotFoundException;
import com.janwypych.ProjectManagementApi.services.workspaceMember.WorkspaceMemberService;
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
public class DeleteWorkspaceMemberTests extends BaseTestWorkspaceMembers {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkspaceMemberService workspaceMemberService;

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

    private void performDelete(Long workspaceId, Long memberId, ResultMatcher... matchers) throws Exception {
        var result = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/workspaces/{workspaceId}/members/{memberId}", workspaceId, memberId)
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    public void shouldReturn401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/workspaces/1/members/1"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceIsNotFound() throws Exception {
        doThrow(new WorkspaceNotFoundException())
                .when(workspaceMemberService)
                .deleteWorkspaceMember(
                        any(User.class),
                        eq(workspace.getId()),
                        eq(workspaceMember.getId())
                );

        performDelete(workspace.getId(), workspaceMember.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_NOT_FOUND"),
                jsonPath("$.message").value("Workspace not found"));
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceMemberIsNotFound() throws Exception {
        doThrow(new WorkspaceMemberNotFoundException())
                .when(workspaceMemberService)
                .deleteWorkspaceMember(
                        any(User.class),
                        eq(workspace.getId()),
                        eq(workspaceMember.getId())
                );

        performDelete(workspace.getId(), workspaceMember.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_MEMBER_NOT_FOUND"),
                jsonPath("$.message").value("Workspace member not found"));
    }

    @Test
    public void shouldReturnHttp409WhenWorkspaceMemberWasAlreadyDeleted() throws Exception {
        doThrow(new WorkspaceMemberAlreadyDeletedException())
                .when(workspaceMemberService)
                .deleteWorkspaceMember(
                        any(User.class),
                        eq(workspace.getId()),
                        eq(workspaceMember.getId())
                );

        performDelete(workspace.getId(), workspaceMember.getId(),
                status().isConflict(),
                jsonPath("$.error").value("WORKSPACE_MEMBER_ALREADY_DELETED"),
                jsonPath("$.message").value("Workspace member has already been deleted"));
    }

    @Test
    public void shouldReturnHttp204WhenRequestIsValid() throws Exception {
        performDelete(workspace.getId(), workspaceMember.getId(),
                status().isNoContent());
    }
}
