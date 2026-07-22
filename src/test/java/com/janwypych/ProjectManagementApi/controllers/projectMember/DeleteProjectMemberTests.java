package com.janwypych.ProjectManagementApi.controllers.projectMember;

import com.janwypych.ProjectManagementApi.BaseTest.projectMember.BaseTestProjectMember;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.project.ProjectNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.projectMember.ProjectMemberAlreadyDeletedException;
import com.janwypych.ProjectManagementApi.exceptions.projectMember.ProjectMemberNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.services.projectMember.ProjectMemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Pageable;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DeleteProjectMemberTests extends BaseTestProjectMember {
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

    private void performDelete(Long workspaceId, Long projectId, Long memberId, ResultMatcher... matchers) throws Exception {

        var result = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/workspaces/{workspaceId}/projects/{projectId}/members/{memberId}", workspaceId, projectId, memberId)
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    public void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/workspaces/1/projects/1/members/1"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceIsNotFound() throws Exception {
        doThrow(new WorkspaceNotFoundException())
                .when(projectMemberService)
                .deleteProjectMember(
                        any(User.class),
                        eq(workspace.getId()),
                        eq(project.getId()),
                        eq(projectMember.getId()));

        performDelete(workspace.getId(), project.getId(), projectMember.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_NOT_FOUND"),
                jsonPath("$.message").value("Workspace not found"));
    }

    @Test
    public void shouldReturnHttp404WhenProjectIsNotFound() throws Exception {
        doThrow(new ProjectNotFoundException())
                .when(projectMemberService)
                .deleteProjectMember(
                        any(User.class),
                        eq(workspace.getId()),
                        eq(project.getId()),
                        eq(projectMember.getId()));

        performDelete(workspace.getId(), project.getId(), projectMember.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("PROJECT_NOT_FOUND"),
                jsonPath("$.message").value("Project not found"));
    }


    @Test
    public void shouldReturnHttp404WhenProjectMemberIsNotFound() throws Exception {
        doThrow(new ProjectMemberNotFoundException())
                .when(projectMemberService)
                .deleteProjectMember(
                        any(User.class),
                        eq(workspace.getId()),
                        eq(project.getId()),
                        eq(projectMember.getId()));

        performDelete(workspace.getId(), project.getId(), projectMember.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("PROJECT_MEMBER_NOT_FOUND"),
                jsonPath("$.message").value("Project member not found"));
    }

    @Test
    public void shouldReturnHttp409WhenProjectMemberWasAlreadyDeleted() throws Exception {
        doThrow(new ProjectMemberAlreadyDeletedException())
                .when(projectMemberService)
                .deleteProjectMember(
                        any(User.class),
                        eq(workspace.getId()),
                        eq(project.getId()),
                        eq(projectMember.getId()));

        performDelete(workspace.getId(), project.getId(), projectMember.getId(),
                status().isConflict(),
                jsonPath("$.error").value("PROJECT_MEMBER_ALREADY_DELETED"),
                jsonPath("$.message").value("Project member has already been deleted"));
    }

    @Test
    public void shouldReturnHttp204WhenRequestIsValid() throws Exception {
        performDelete(workspace.getId(), project.getId(), projectMember.getId(),
                status().isNoContent());
    }
}
