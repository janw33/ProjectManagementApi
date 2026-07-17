package com.janwypych.ProjectManagementApi.controllers.projectMember;

import com.janwypych.ProjectManagementApi.BaseTest.projectMember.BaseTestProjectMember;
import com.janwypych.ProjectManagementApi.dtos.projectMember.CreateProjectMemberRequest;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.project.ProjectNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.projectMember.ProjectMemberAlreadyExistsException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspaceMember.WorkspaceMemberNotFoundException;
import com.janwypych.ProjectManagementApi.services.projectMember.ProjectMemberService;
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
public class CreateProjectMemberTests extends BaseTestProjectMember {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    private void performCreate(Long workspaceId, Long projectId, CreateProjectMemberRequest request, ResultMatcher... matchers) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        var result = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/workspaces/{workspaceId}/projects/{projectId}/members", workspaceId, projectId)
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
        String requestJson = objectMapper.writeValueAsString(createProjectMemberRequest);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/workspaces/1/projects/1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturn400WhenWorkspaceMemberIdIsNull() throws Exception {
        createProjectMemberRequest.setWorkspaceMemberId(null);
        performCreate(workspace.getId(), project.getId(), createProjectMemberRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.workspaceMemberId").value("Workspace member id cannot be null"));
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceIsNotFound() throws Exception {
        doThrow(new WorkspaceNotFoundException())
                .when(projectMemberService)
                .createProjectMember(
                        any(User.class),
                        eq(createProjectMemberRequest),
                        eq(workspace.getId()),
                        eq(project.getId())
                );

        performCreate(workspace.getId(), project.getId(), createProjectMemberRequest,
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_NOT_FOUND"),
                jsonPath("$.message").value("Workspace not found"));
    }

    @Test
    public void shouldReturnHttp404WhenProjectIsNotFound() throws Exception {
        doThrow(new ProjectNotFoundException())
                .when(projectMemberService)
                .createProjectMember(
                        any(User.class),
                        eq(createProjectMemberRequest),
                        eq(workspace.getId()),
                        eq(project.getId())
                );

        performCreate(workspace.getId(), project.getId(), createProjectMemberRequest,
                status().isNotFound(),
                jsonPath("$.error").value("PROJECT_NOT_FOUND"),
                jsonPath("$.message").value("Project not found"));
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceMemberIsNotFound() throws Exception {
        doThrow(new WorkspaceMemberNotFoundException())
                .when(projectMemberService)
                .createProjectMember(
                        any(User.class),
                        eq(createProjectMemberRequest),
                        eq(workspace.getId()),
                        eq(project.getId())
                );

        performCreate(workspace.getId(), project.getId(), createProjectMemberRequest,
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_MEMBER_NOT_FOUND"),
                jsonPath("$.message").value("Workspace member not found"));
    }

    @Test
    public void shouldReturnHttp409WhenProjectMemberAlreadyExists() throws Exception {
        doThrow(new ProjectMemberAlreadyExistsException())
                .when(projectMemberService)
                .createProjectMember(
                        any(User.class),
                        eq(createProjectMemberRequest),
                        eq(workspace.getId()),
                        eq(project.getId())
                );

        performCreate(workspace.getId(), project.getId(), createProjectMemberRequest,
                status().isConflict(),
                jsonPath("$.error").value("PROJECT_MEMBER_ALREADY_EXISTS"),
                jsonPath("$.message").value("Project member already exists"));
    }

    @Test
    public void shouldReturnHttp201WhenRequestIsValid() throws Exception {
        performCreate(workspace.getId(), project.getId(), createProjectMemberRequest,
                status().isCreated());
    }
}
