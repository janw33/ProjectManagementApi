package com.janwypych.ProjectManagementApi.controllers.workspaceMembers;

import com.janwypych.ProjectManagementApi.BaseTest.workspaceMember.BaseTestWorkspaceMembers;
import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.workspace.CreateWorkspaceRequest;
import com.janwypych.ProjectManagementApi.dtos.workspaceMember.UpdateWorkspaceMemberRequest;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.projectMember.ProjectMemberNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspaceMember.WorkspaceMemberNotFoundException;
import com.janwypych.ProjectManagementApi.services.workspaceMember.WorkspaceMemberService;
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
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UpdateWorkspaceMemberTests extends BaseTestWorkspaceMembers {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    private void performUpdate(Long workspaceId, Long memberId, UpdateWorkspaceMemberRequest request, ResultMatcher... matchers) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        var result = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/v1/workspaces/{workspaceId}/members/{memberId}", workspaceId, memberId)
                        .with(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    public void shouldReturn401WhenUserIsUnauthenticated() throws Exception {
        String requestJson = objectMapper.writeValueAsString(updateWorkspaceMemberRequest);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/v1/workspaces/1/members/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceIsNotFound() throws Exception {
        doThrow(new WorkspaceNotFoundException())
                .when(workspaceMemberService)
                .updateWorkspaceMember(
                        any(User.class),
                        eq(updateWorkspaceMemberRequest),
                        eq(workspace.getId()),
                        eq(workspaceMember.getId())
                );

        performUpdate(workspace.getId(), workspaceMember.getId(), updateWorkspaceMemberRequest,
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_NOT_FOUND"),
                jsonPath("$.message").value("Workspace not found"));
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceMemberIsNotFound() throws Exception {
        doThrow(new WorkspaceMemberNotFoundException())
                .when(workspaceMemberService)
                .updateWorkspaceMember(
                        any(User.class),
                        eq(updateWorkspaceMemberRequest),
                        eq(workspace.getId()),
                        eq(workspaceMember.getId())
                );

        performUpdate(workspace.getId(), workspaceMember.getId(), updateWorkspaceMemberRequest,
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_MEMBER_NOT_FOUND"),
                jsonPath("$.message").value("Workspace member not found"));
    }

    @Test
    public void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        performUpdate(workspace.getId(), workspaceMember.getId(), updateWorkspaceMemberRequest,
                status().isOk());
    }
}
