package com.janwypych.ProjectManagementApi.controllers.project;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.project.ProjectIdResponse;
import com.janwypych.ProjectManagementApi.dtos.project.UpdateProjectRequest;
import com.janwypych.ProjectManagementApi.entities.user.User;
import com.janwypych.ProjectManagementApi.exceptions.Project.ProjectNotFoundException;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.services.project.ProjectService;
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
public class UpdateProjectTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

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

    private void performUpdate(Long workspaceId, Long projectId, UpdateProjectRequest request, ResultMatcher... matchers) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        var result = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/v1/workspaces/{workspaceId}/projects/{projectId}", workspaceId, projectId)
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
        UpdateProjectRequest request = TestDataUtil.updateProjectRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/v1/workspaces/1/projects/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp400WhenNameIsTooShort() throws Exception {
        Long workspaceId = 1L;
        Long projectId = 1L;

        UpdateProjectRequest request = TestDataUtil.updateProjectRequest();
        request.setName("a");

        performUpdate(workspaceId,
                projectId,
                request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.name").value("Name must be between 2 and 100 characters"));
    }

    @Test
    public void shouldReturnHttp400WhenNameIsTooLong() throws Exception {
        Long workspaceId = 1L;
        Long projectId = 1L;

        UpdateProjectRequest request = TestDataUtil.updateProjectRequest();
        request.setName("a".repeat(101));

        performUpdate(workspaceId,
                projectId,
                request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.name").value("Name must be between 2 and 100 characters"));
    }

    @Test
    public void shouldReturnHttp400WhenNameContainsOnlyWhitespaces() throws Exception {
        Long workspaceId = 1L;
        Long projectId = 1L;

        UpdateProjectRequest request = TestDataUtil.updateProjectRequest();
        request.setName("   ");

        performUpdate(workspaceId,
                projectId,
                request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.name").value("Name cannot contain only whitespace"));
    }

    @Test
    public void shouldReturnHttp400WhenDescriptionIsTooLong() throws Exception {
        Long workspaceId = 1L;
        Long projectId = 1L;

        UpdateProjectRequest request = TestDataUtil.updateProjectRequest();
        request.setDescription("a".repeat(501));

        performUpdate(workspaceId,
                projectId,
                request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.description").value("Description must be at most 500 characters long"));
    }

    @Test
    public void shouldReturnHttp400WhenDescriptionContainsOnlyWhitespaces() throws Exception {
        Long workspaceId = 1L;
        Long projectId = 1L;

        UpdateProjectRequest request = TestDataUtil.updateProjectRequest();
        request.setDescription("   ");

        performUpdate(workspaceId,
                projectId,
                request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.description").value("Description cannot contain only whitespace"));
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceIsNotFound() throws Exception {
        Long workspaceId = 1L;
        Long projectId = 1L;

        UpdateProjectRequest request = TestDataUtil.updateProjectRequest();

        when(projectService.updateProject(any(User.class), eq(request), eq(workspaceId), eq(projectId)))
                .thenThrow(new WorkspaceNotFoundException("Workspace not found"));

        performUpdate(workspaceId,
                projectId,
                request,
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_NOT_FOUND"),
                jsonPath("$.message").value("Workspace not found"));
    }

    @Test
    public void shouldReturnHttp404WhenProjectIsNotFound() throws Exception {
        Long workspaceId = 1L;
        Long projectId = 1L;

        UpdateProjectRequest request = TestDataUtil.updateProjectRequest();

        when(projectService.updateProject(any(User.class), eq(request), eq(workspaceId), eq(projectId)))
                .thenThrow(new ProjectNotFoundException());

        performUpdate(workspaceId,
                projectId,
                request,
                status().isNotFound(),
                jsonPath("$.error").value("PROJECT_NOT_FOUND"),
                jsonPath("$.message").value("Project not found"));
    }

    @Test
    public void shouldReturnHttp200WhenProjectIsFound() throws Exception {
        Long workspaceId = 1L;
        Long projectId = 1L;

        UpdateProjectRequest request = TestDataUtil.updateProjectRequest();

        ProjectIdResponse projectIdResponse = new ProjectIdResponse(1L);

        when(projectService.updateProject(any(User.class), eq(request), eq(workspaceId), eq(projectId)))
                .thenReturn(projectIdResponse);

        performUpdate(workspaceId,
                projectId,
                request,
                status().isOk(),
                jsonPath("$.id").value(1L));
    }

    @Test
    public void shouldReturnHttp200WhenNameIsNull() throws Exception {
        Long workspaceId = 1L;
        Long projectId = 1L;

        UpdateProjectRequest request = TestDataUtil.updateProjectRequest();
        request.setName(null);

        ProjectIdResponse projectIdResponse = new ProjectIdResponse(1L);

        when(projectService.updateProject(any(User.class), eq(request), eq(workspaceId), eq(projectId)))
                .thenReturn(projectIdResponse);

        performUpdate(workspaceId,
                projectId,
                request,
                status().isOk(),
                jsonPath("$.id").value(1L));
    }

    @Test
    public void shouldReturnHttp200WhenDescriptionIsNull() throws Exception {
        Long workspaceId = 1L;
        Long projectId = 1L;

        UpdateProjectRequest request = TestDataUtil.updateProjectRequest();
        request.setDescription(null);

        ProjectIdResponse projectIdResponse = new ProjectIdResponse(1L);

        when(projectService.updateProject(any(User.class), eq(request), eq(workspaceId), eq(projectId)))
                .thenReturn(projectIdResponse);

        performUpdate(workspaceId,
                projectId,
                request,
                status().isOk(),
                jsonPath("$.id").value(1L));
    }

    @Test
    public void shouldReturnHttp200WhenNameAndDescriptionAreNull() throws Exception {
        Long workspaceId = 1L;
        Long projectId = 1L;

        UpdateProjectRequest request = TestDataUtil.updateProjectRequest();
        request.setName(null);
        request.setDescription(null);

        ProjectIdResponse projectIdResponse = new ProjectIdResponse(1L);

        when(projectService.updateProject(any(User.class), eq(request), eq(workspaceId), eq(projectId)))
                .thenReturn(projectIdResponse);

        performUpdate(workspaceId,
                projectId,
                request,
                status().isOk(),
                jsonPath("$.id").value(1L));
    }
}