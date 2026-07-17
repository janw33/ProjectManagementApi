package com.janwypych.ProjectManagementApi.controllers.projectMember;

import com.janwypych.ProjectManagementApi.BaseTest.projectMember.BaseTestProjectMember;
import com.janwypych.ProjectManagementApi.dtos.projectMember.ProjectMemberResponse;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.project.ProjectNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.services.projectMember.ProjectMemberService;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetProjectMembersTests extends BaseTestProjectMember {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectMemberService projectMemberService;

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

    private void performGet(Long workspaceId, Long projectId, ResultMatcher... matchers) throws Exception {

        var result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/workspaces/{workspaceId}/projects/{projectId}/members", workspaceId, projectId)
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    public void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/workspaces/1/projects/1/members"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceIsNotFound() throws Exception {
        when(projectMemberService.getProjectMembers(any(User.class), eq(workspace.getId()), eq(project.getId()), any(Pageable.class)))
                .thenThrow(new WorkspaceNotFoundException());

        performGet(workspace.getId(), project.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_NOT_FOUND"),
                jsonPath("$.message").value("Workspace not found"));
    }

    @Test
    public void shouldReturnHttp404WhenProjectIsNotFound() throws Exception {
        when(projectMemberService.getProjectMembers(any(User.class), eq(workspace.getId()), eq(project.getId()), any(Pageable.class)))
                .thenThrow(new ProjectNotFoundException());

        performGet(workspace.getId(), project.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("PROJECT_NOT_FOUND"),
                jsonPath("$.message").value("Project not found"));
    }

    @Test
    public void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        ProjectMemberResponse projectMemberResponse = ProjectMemberResponse.builder()
                .id(projectMember.getId())
                .username(projectMember.getWorkspaceMember().getUser().getUsername())
                .workspaceRole(projectMember.getWorkspaceMember().getRole())
                .joinedAt(projectMember.getJoinedAt())
                .build();

        when(projectMemberService.getProjectMembers(any(User.class), eq(workspace.getId()), eq(project.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(projectMemberResponse)));

        performGet(workspace.getId(), project.getId(),
                status().isOk(),
                jsonPath("$.content").isArray(),
                jsonPath("$.totalElements").value(1),
                jsonPath("$.content[0].id").value(projectMember.getId()),
                jsonPath("$.content[0].username").value(projectMember.getWorkspaceMember().getUser().getUsername()),
                jsonPath("$.content[0].workspaceRole").value(projectMember.getWorkspaceMember().getRole().toString()),
                jsonPath("$.content[0].joinedAt").isNotEmpty());

    }
}