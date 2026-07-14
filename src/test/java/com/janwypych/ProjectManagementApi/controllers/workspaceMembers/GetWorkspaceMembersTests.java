package com.janwypych.ProjectManagementApi.controllers.workspaceMembers;


import com.janwypych.ProjectManagementApi.BaseTestWorkspaceMembers;
import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.workspaceMember.WorkspaceMemberSummaryResponse;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.services.workspaceMember.WorkspaceMemberService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetWorkspaceMembersTests extends BaseTestWorkspaceMembers {
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

    private void performGet(Long workspaceId, ResultMatcher... matchers) throws Exception {
        var result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/workspaces/{workspaceId}/members", workspaceId)
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    public void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/workspaces/1/members"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceIsNotFound() throws Exception {
        when(workspaceMemberService.getWorkspaceMembers(any(User.class), eq(workspace.getId()), any(Pageable.class)))
                .thenThrow(new WorkspaceNotFoundException());

        performGet(workspace.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_NOT_FOUND"),
                jsonPath("$.message").value("Workspace not found"));
    }

    @Test
    public void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        WorkspaceMemberSummaryResponse workspaceMemberSummaryResponse = WorkspaceMemberSummaryResponse.builder()
                .id(workspaceMember.getId())
                .username(workspaceMember.getUser().getUsername())
                .role(workspaceMember.getRole())
                .build();

        when(workspaceMemberService.getWorkspaceMembers(any(User.class), eq(workspace.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(workspaceMemberSummaryResponse)));

        performGet(workspace.getId(),
                status().isOk(),
                jsonPath("$.content").isArray(),
                jsonPath("$.totalElements").value(1),
                jsonPath("$.content[0].id").value(workspaceMember.getId()),
                jsonPath("$.content[0].username").value(workspaceMember.getUser().getUsername()),
                jsonPath("$.content[0].role").value(workspaceMember.getRole().toString()));
    }
}
