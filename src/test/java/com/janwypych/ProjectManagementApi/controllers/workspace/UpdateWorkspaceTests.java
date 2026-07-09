package com.janwypych.ProjectManagementApi.controllers.workspace;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.workspace.UpdateWorkspaceRequest;
import com.janwypych.ProjectManagementApi.dtos.workspace.WorkspaceIdResponse;
import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.exceptions.workspace.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.services.WorkspaceService;
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
public class UpdateWorkspaceTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WorkspaceService workspaceService;

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

    private void performUpdate(Long workspaceId, UpdateWorkspaceRequest request, ResultMatcher... matchers) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        var result = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/v1/workspaces/{workspaceId}", workspaceId)
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
        UpdateWorkspaceRequest request = TestDataUtil.updateWorkspaceRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/v1/workspaces/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp400WhenNameIsOnlyWhitespaces() throws Exception {
        UpdateWorkspaceRequest request = TestDataUtil.updateWorkspaceRequest();
        Long workspaceId = 1L;
        request.setName("    ");
        performUpdate(workspaceId,
                request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.name").value("Name cannot contain only whitespace"));
    }

    @Test
    public void shouldReturnHttp400WhenNameIsTooShort() throws Exception {
        UpdateWorkspaceRequest request = TestDataUtil.updateWorkspaceRequest();
        Long workspaceId = 1L;
        request.setName("a");
        performUpdate(workspaceId,
                request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.name").value("Name must be between 2 and 100 characters"));
    }

    @Test
    public void shouldReturnHttp400WhenNameIsTooLong() throws Exception {
        UpdateWorkspaceRequest request = TestDataUtil.updateWorkspaceRequest();
        Long workspaceId = 1L;
        request.setName("a".repeat(101));
        performUpdate(workspaceId,
                request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.name").value("Name must be between 2 and 100 characters"));
    }


    @Test
    public void shouldReturnHttp400WhenDescriptionIsTooLong() throws Exception {
        UpdateWorkspaceRequest request = TestDataUtil.updateWorkspaceRequest();
        Long workspaceId = 1L;
        request.setDescription("a".repeat(501));
        performUpdate(workspaceId,
                request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.description").value("Description must be at most 500 characters long"));
    }

    @Test
    public void shouldReturnHttp400WhenDescriptionContainsOnlyWhitespace() throws Exception {
        UpdateWorkspaceRequest request = TestDataUtil.updateWorkspaceRequest();
        Long workspaceId = 1L;
        request.setDescription("     ");
        performUpdate(workspaceId,
                request,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.description").value("Description cannot contain only whitespace"));
    }

    @Test
    public void shouldReturn404WhenWorkspaceIsNotFound() throws Exception{
        UpdateWorkspaceRequest request = TestDataUtil.updateWorkspaceRequest();
        Long workspaceId = 1L;

        when(workspaceService.updateWorkspace(any(User.class), eq(request), eq(workspaceId)))
                .thenThrow(new WorkspaceNotFoundException("Workspace not found"));

        performUpdate(workspaceId,
                request,
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_NOT_FOUND"),
                jsonPath("$.message").value("Workspace not found"));
    }

    @Test
    public void shouldReturnHttp200WhenRequestIsValid() throws Exception{
        UpdateWorkspaceRequest request = TestDataUtil.updateWorkspaceRequest();
        Long workspaceId = 1L;
        WorkspaceIdResponse workspaceIdResponse = new WorkspaceIdResponse(1L);

        when(workspaceService.updateWorkspace(any(User.class), eq(request), eq(workspaceId)))
                .thenReturn(workspaceIdResponse);

        performUpdate(workspaceId,
                request,
                status().isOk(),
                jsonPath("$.id").value(1L));
    }

    @Test
    public void shouldReturnHttp200WhenNameIsNull() throws Exception{
        UpdateWorkspaceRequest request = TestDataUtil.updateWorkspaceRequest();
        request.setName(null);
        Long workspaceId = 1L;
        WorkspaceIdResponse workspaceIdResponse = new WorkspaceIdResponse(1L);

        when(workspaceService.updateWorkspace(any(User.class), eq(request), eq(workspaceId)))
                .thenReturn(workspaceIdResponse);

        performUpdate(workspaceId,
                request,
                status().isOk(),
                jsonPath("$.id").value(1L));
    }

    @Test
    public void shouldReturnHttp200WhenDescriptionIsNull() throws Exception{
        UpdateWorkspaceRequest request = TestDataUtil.updateWorkspaceRequest();
        request.setDescription(null);
        Long workspaceId = 1L;
        WorkspaceIdResponse workspaceIdResponse = new WorkspaceIdResponse(1L);

        when(workspaceService.updateWorkspace(any(User.class), eq(request), eq(workspaceId)))
                .thenReturn(workspaceIdResponse);

        performUpdate(workspaceId,
                request,
                status().isOk(),
                jsonPath("$.id").value(1L));
    }

    @Test
    public void shouldReturnHttp200WhenDescriptionAndNameAreNull() throws Exception{
        UpdateWorkspaceRequest request = TestDataUtil.updateWorkspaceRequest();
        request.setName(null);
        request.setDescription(null);
        Long workspaceId = 1L;
        WorkspaceIdResponse workspaceIdResponse = new WorkspaceIdResponse(1L);

        when(workspaceService.updateWorkspace(any(User.class), eq(request), eq(workspaceId)))
                .thenReturn(workspaceIdResponse);

        performUpdate(workspaceId,
                request,
                status().isOk(),
                jsonPath("$.id").value(1L));
    }
}
