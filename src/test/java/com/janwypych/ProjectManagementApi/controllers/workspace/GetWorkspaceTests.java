package com.janwypych.ProjectManagementApi.controllers.workspace;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.workspace.WorkspaceDetailsResponse;
import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.exceptions.WorkspaceNotFoundException;
import com.janwypych.ProjectManagementApi.services.WorkspaceService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetWorkspaceTests {
    @Autowired
    private MockMvc mockMvc;

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

    private void performGet(Long workspaceId, ResultMatcher... matchers) throws Exception {
        var result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/workspaces/" + workspaceId)
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    public void shouldReturn401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/workspaces/1"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp404WhenWorkspaceIsNotFound() throws Exception {
        Long workspaceId = 1L;

        when(workspaceService.getWorkspace(any(User.class), eq(workspaceId)))
                .thenThrow(new WorkspaceNotFoundException("Workspace not found"));

        performGet(workspaceId,
                status().isNotFound(),
                jsonPath("$.error").value("WORKSPACE_NOT_FOUND"),
                jsonPath("$.message").value("Workspace not found"));
    }

    @Test
    public void shouldReturnHttp201WhenWorkspaceExists() throws Exception {
        Long workspaceId = 1L;

        WorkspaceDetailsResponse workspaceDetailsResponse = TestDataUtil.workspaceDetailsResponse();

        when(workspaceService.getWorkspace(any(User.class), eq(workspaceId)))
                .thenReturn(workspaceDetailsResponse);

        performGet(workspaceId,
                status().isOk(),
                jsonPath("$.id").value(1L),
                jsonPath("$.name").value("test"),
                jsonPath("$.description").value("test"),
                jsonPath("$.createdAt").isNotEmpty(),
                jsonPath("$.updatedAt").isNotEmpty(),
                jsonPath("$.role").value("OWNER"));
    }
}