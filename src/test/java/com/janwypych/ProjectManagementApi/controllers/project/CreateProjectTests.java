package com.janwypych.ProjectManagementApi.controllers.project;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.Project.CreateProjectRequest;
import com.janwypych.ProjectManagementApi.dtos.Project.ProjectIdResponse;
import com.janwypych.ProjectManagementApi.entities.user.User;
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
public class CreateProjectTests {
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

    private void performCreate(Long workspaceId ,CreateProjectRequest request, ResultMatcher... matchers) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        var result = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/workspaces/{workspaceId}/projects", workspaceId)
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
        CreateProjectRequest request = TestDataUtil.createProjectRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/workspaces/1/projects")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp400WhenNameIsNull() throws Exception {
        CreateProjectRequest request = TestDataUtil.createProjectRequest();
        Long workspaceId = 1L;
        request.setName(null);
        performCreate(workspaceId,
                request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.name").value("Name cannot be blank"));
    }

    @Test
    public void shouldReturnHttp400WhenNameIsBlank() throws Exception {
        CreateProjectRequest request = TestDataUtil.createProjectRequest();
        Long workspaceId = 1L;
        request.setName("     ");
        performCreate(workspaceId,
                request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.name").value("Name cannot be blank"));
    }

    @Test
    public void shouldReturnHttp400WhenNameIsTooShort() throws Exception {
        CreateProjectRequest request = TestDataUtil.createProjectRequest();
        Long workspaceId = 1L;
        request.setName("a");
        performCreate(workspaceId,
                request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.name").value("Name must be between 2 and 100 characters"));
    }

    @Test
    public void shouldReturnHttp400WhenNameIsTooLong() throws Exception {
        CreateProjectRequest request = TestDataUtil.createProjectRequest();
        Long workspaceId = 1L;
        request.setName("a".repeat(101));
        performCreate(workspaceId,
                request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.name").value("Name must be between 2 and 100 characters"));
    }

    @Test
    public void shouldReturnHttp400WhenDescriptionContainsOnlyWhitespaces() throws Exception {
        CreateProjectRequest request = TestDataUtil.createProjectRequest();
        Long workspaceId = 1L;
        request.setDescription("    ");
        performCreate(workspaceId,
                request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.description").value("Description cannot contain only whitespace"));
    }

    @Test
    public void shouldReturnHttp400WhenDescriptionIsTooLong() throws Exception {
        CreateProjectRequest request = TestDataUtil.createProjectRequest();
        Long workspaceId = 1L;
        request.setDescription("a".repeat(501));
        performCreate(workspaceId,
                request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.description").value("Description must be at most 500 characters long"));
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceIsNotFound() throws Exception {
        CreateProjectRequest request = TestDataUtil.createProjectRequest();
        Long workspaceId = 1L;

        when(projectService.createProject(any(User.class), eq(request), eq(workspaceId)))
                .thenThrow(new WorkspaceNotFoundException("Workspace not found"));

        performCreate(workspaceId,
                request,
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_NOT_FOUND"),
                jsonPath("$.message").value("Workspace not found"));
    }

    @Test
    public void shouldReturnHttp201WhenRequestIsValid() throws Exception {
        CreateProjectRequest request = TestDataUtil.createProjectRequest();
        Long workspaceId = 1L;

        when(projectService.createProject(any(User.class), eq(request), eq(workspaceId)))
                .thenReturn(new ProjectIdResponse(1L));

        performCreate(workspaceId,
                request,
                status().isCreated(),
                jsonPath("$.id").value(1L));
    }
}
