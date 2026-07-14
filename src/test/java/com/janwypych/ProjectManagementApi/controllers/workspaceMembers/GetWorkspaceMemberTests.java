package com.janwypych.ProjectManagementApi.controllers.workspaceMembers;

import com.janwypych.ProjectManagementApi.BaseTest.workspaceMember.BaseTestWorkspaceMembers;
import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.workspaceMember.WorkspaceMemberDetailsResponse;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetWorkspaceMemberTests extends BaseTestWorkspaceMembers {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkspaceMemberService workspaceMemberService;

    private Authentication createAuthentication() {
        User user = TestDataUtil.user();

        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                null
        );
    }

    private RequestPostProcessor authenticatedUser() {
        return authentication(createAuthentication());
    }

    private void performGet(Long workspaceId, Long memberId, ResultMatcher... matchers) throws Exception {
        var result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/workspaces/{workspaceId}/members/{memberId}", workspaceId, memberId)
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    public void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/workspaces/1/members/1"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceIsNotFound() throws Exception {
        when(workspaceMemberService.getWorkspaceMember(any(User.class), eq(workspace.getId()), eq(workspaceMember.getId())))
                .thenThrow(new WorkspaceNotFoundException());

        performGet(workspace.getId(), workspaceMember.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_NOT_FOUND"),
                jsonPath("$.message").value("Workspace not found"));
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceMemberIsNotFound() throws Exception {
        when(workspaceMemberService.getWorkspaceMember(any(User.class), eq(workspace.getId()), eq(workspaceMember.getId())))
                .thenThrow(new WorkspaceMemberNotFoundException());

        performGet(workspace.getId(), workspaceMember.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_MEMBER_NOT_FOUND"),
                jsonPath("$.message").value("Workspace member not found"));
    }

    @Test
    public void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        WorkspaceMemberDetailsResponse workspaceMemberDetailsResponse = WorkspaceMemberDetailsResponse.builder()
                .id(workspaceMember.getId())
                .username(workspaceMember.getUser().getUsername())
                .email(workspaceMember.getUser().getEmail())
                .role(workspaceMember.getRole())
                .joinedAt(workspaceMember.getJoinedAt())
                .build();

        when(workspaceMemberService.getWorkspaceMember(any(User.class), eq(workspace.getId()), eq(workspaceMember.getId())))
                .thenReturn(workspaceMemberDetailsResponse);

        performGet(workspace.getId(), workspaceMember.getId(),
                status().isOk(),
                jsonPath("$.id").value(workspaceMember.getId()),
                jsonPath("$.username").value(workspaceMember.getUser().getUsername()),
                jsonPath("$.email").value(workspaceMember.getUser().getEmail()),
                jsonPath("$.role").value(workspaceMember.getRole().toString()),
                jsonPath("$.joinedAt").isNotEmpty());
    }
}
